package com.example.simple_survey_tool.controller_layer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.simple_survey_tool.model_layer.Answer;
import com.example.simple_survey_tool.model_layer.Question;
import com.example.simple_survey_tool.model_layer.Submission;
import com.example.simple_survey_tool.model_layer.SubmitAnswersRequest;
import com.example.simple_survey_tool.model_layer.Survey;
import com.example.simple_survey_tool.service_layer.SubmissionService;
import com.example.simple_survey_tool.service_layer.SurveyService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Controller responsible for handling survey-related HTTP requests.
 */
@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
@Validated
public class SurveyController {

    private final SurveyService surveyService;
    private final SubmissionService submissionService;

    /**
     * Creates a new survey.
     * @param survey - Survey entity to be saved.
     * @return ResponseEntity with saved Survey entity.
     */
    @PostMapping("/")
    public ResponseEntity<Survey> saveSurvey(@RequestBody Survey survey) {
        Survey savedSurvey = surveyService.saveSurvey(survey);
        return ResponseEntity.ok(savedSurvey);
    }

    /**
     * Retrieves a survey by its ID.
     * @param id - Survey ID.
     * @return ResponseEntity with the Survey or a NOT_FOUND status if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Survey> getSurveyById(@PathVariable UUID id) {
        try {
            Survey survey = surveyService.getSurveyById(id);
            return ResponseEntity.ok(survey);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Submits answers for a survey.
     * @param submitAnswersRequest - Request containing survey ID and answers.
     * @return ResponseEntity with success or error message.
     */
    @PostMapping("/submit")
    public ResponseEntity<String> submitAnswers(@RequestBody SubmitAnswersRequest submitAnswersRequest) {
        try {
            Submission submission = submissionService.createSubmission(submitAnswersRequest.getSurveyId());
            Map<UUID, String> answers = submitAnswersRequest.getAnswers();

            // Process each answer and save them
            saveAnswers(submission, submitAnswersRequest.getSurveyId(), answers);

            // Update the survey response count
            surveyService.updateSurvey(surveyService.getSurveyById(submitAnswersRequest.getSurveyId()));

            return ResponseEntity.ok("Answers submitted successfully.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Survey not found.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid answers provided.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit answers.");
        }
    }

    /**
     * Helper method to save answers for each question in the survey.
     * @param submission - The Submission entity to associate answers with.
     * @param surveyId - The ID of the survey being submitted.
     * @param answers - Map of answers, where the key is the question ID and the value is the answer text.
     */
    private void saveAnswers(Submission submission, UUID surveyId, Map<UUID, String> answers) {
        answers.forEach((questionId, answerText) -> {
            // Retrieve the question associated with the answer
            Question question = surveyService.getQuestionsBySurveyId(surveyId)
                .stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Question not found"));

            // Create the answer entity and associate it with the submission and question
            Answer answer = new Answer();
            answer.setSubmission(submission);
            answer.setQuestion(question);
            answer.setAnswer(answerText);

            // Save the answer to the database
            submissionService.saveResponses(List.of(answer));
        });
    }

    /**
     * Retrieves the results for a survey.
     * @param id - Survey ID.
     * @return ResponseEntity with survey results or an error message.
     */
    @GetMapping("/{id}/results")
    public ResponseEntity<?> getSurveyResults(@PathVariable UUID id) {
        try {
            Survey survey = surveyService.getSurveyById(id);
            List<Question> questions = surveyService.getQuestionsBySurveyId(id);
            List<Answer> allAnswers = submissionService.getAnswersBySurveyId(id);

            Map<UUID, Map<String, Long>> groupedAnswers = groupAnswersByQuestion(allAnswers);

            // Prepare the result summary for each question
            List<Map<String, Object>> resultSummary = generateResultSummary(questions, groupedAnswers);

            // Construct final response object with survey metadata and results
            Map<String, Object> response = buildSurveyResultResponse(survey, resultSummary);

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve survey results.");
        }
    }

    /**
     * Helper method to group answers by question.
     * @param allAnswers - List of all answers for the survey.
     * @return Map of answers grouped by question ID.
     */
    private Map<UUID, Map<String, Long>> groupAnswersByQuestion(List<Answer> allAnswers) {
        return allAnswers.stream()
            .collect(Collectors.groupingBy(
                answer -> answer.getQuestion().getId(),
                Collectors.groupingBy(
                    Answer::getAnswer,
                    Collectors.counting()
                )
            ));
    }

    /**
     * Generates a summary of the results for each question in the survey.
     * @param questions - List of survey questions.
     * @param groupedAnswers - Map of grouped answers by question.
     * @return List of result summaries for each question.
     */
    private List<Map<String, Object>> generateResultSummary(List<Question> questions, Map<UUID, Map<String, Long>> groupedAnswers) {
        return questions.stream().map(question -> {
            Map<String, String> optionMap = createOptionMap(question.getResponseOptions());

            Map<String, Long> rawResults = groupedAnswers.getOrDefault(question.getId(), Map.of());

            // Map numeric answers to corresponding labels
            Map<String, Long> mappedResults = mapAnswersToLabels(rawResults, optionMap);

            return Map.of(
                "questionText", question.getText(),
                "responseOptions", question.getResponseOptions(),
                "responses", mappedResults
            );
        }).collect(Collectors.toList());
    }

    /**
     * Creates a map of numeric answer labels to actual response options.
     * @param responseOptions - List of possible response options.
     * @return Map of numeric labels to response options.
     */
    private Map<String, String> createOptionMap(List<String> responseOptions) {
        Map<String, String> optionMap = new HashMap<>();
        for (int i = 0; i < responseOptions.size(); i++) {
            optionMap.put(String.valueOf(i + 1), responseOptions.get(i));
        }
        return optionMap;
    }

    /**
     * Maps raw numeric answers to their corresponding response labels.
     * @param rawResults - Map of raw answers.
     * @param optionMap - Map of numeric labels to response options.
     * @return Mapped answers with labels as keys.
     */
    private Map<String, Long> mapAnswersToLabels(Map<String, Long> rawResults, Map<String, String> optionMap) {
        return rawResults.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> optionMap.getOrDefault(entry.getKey(), entry.getKey()), // Map "1" to "Strongly Disagree"
                Map.Entry::getValue
            ));
    }

    /**
     * Builds the final response structure for survey results.
     * @param survey - The survey entity.
     * @param resultSummary - The summary of survey results.
     * @return The complete response map with survey metadata and results.
     */
    private Map<String, Object> buildSurveyResultResponse(Survey survey, List<Map<String, Object>> resultSummary) {
        Map<String, Object> response = new HashMap<>();
        response.put("survey", Map.of(
            "id", survey.getId(),
            "description", survey.getDescription(),
            "createdAt", survey.getCreatedAt(),
            "updatedAt", survey.getUpdatedAt(),
            "responseCount", survey.getResponse_count()
        ));
        response.put("results", resultSummary);
        return response;
    }

    /**
     * Handles invalid UUID format exceptions.
     * @param ex - The exception object.
     * @return ResponseEntity with an error message.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == UUID.class) {
            return ResponseEntity.badRequest().body("Invalid UUID format: " + ex.getValue());
        }
        return ResponseEntity.badRequest().body("Invalid parameter: " + ex.getValue());
    }
}
