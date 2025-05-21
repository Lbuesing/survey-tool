package com.example.simple_survey_tool.controller_layer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
            submissionService.saveAnswers(submission, submitAnswersRequest.getSurveyId(), answers);

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

            Map<UUID, Map<String, Long>> groupedAnswers = surveyService.groupAnswersByQuestion(allAnswers);

            // Prepare the result summary for each question
            List<Map<String, Object>> resultSummary = surveyService.generateResultSummary(questions, groupedAnswers);

            // Construct final response object with survey metadata and results
            Map<String, Object> response = surveyService.buildSurveyResultResponse(survey, resultSummary);

            return ResponseEntity.ok(response);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to retrieve survey results.");
        }
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
