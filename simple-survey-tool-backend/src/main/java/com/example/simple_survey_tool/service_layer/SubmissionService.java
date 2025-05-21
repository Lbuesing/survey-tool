package com.example.simple_survey_tool.service_layer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.simple_survey_tool.model_layer.Answer;
import com.example.simple_survey_tool.model_layer.Question;
import com.example.simple_survey_tool.model_layer.Submission;
import com.example.simple_survey_tool.model_layer.Survey;
import com.example.simple_survey_tool.repository_layer.AnswerRepo;
import com.example.simple_survey_tool.repository_layer.SubmissionRepo;

import lombok.RequiredArgsConstructor;

/**
 * Service class that handles business logic related to survey submissions and responses.
 */
@RequiredArgsConstructor
@Service
public class SubmissionService {
    private final SubmissionRepo submissionRepo;
    private final AnswerRepo answerRepo;
    private final SurveyService surveyService;

    /**
     * Creates a new submission record for a given survey.
     *
     * @param surveyId The UUID of the survey for which the submission is being made.
     * @return The saved Submission entity.
     */
    public Submission createSubmission(UUID surveyId) {
        Submission submission = new Submission();
        
        // Set the associated survey using its ID
        submission.setSurvey(new Survey(surveyId));
        
        // Set the current timestamp as the submission time
        submission.setSubmittedAt(LocalDateTime.now());

        // Save and return the new submission
        return submissionRepo.save(submission);
    }

    /**
     * Saves a list of responses (answers) to the database.
     *
     * @param answers List of Answer entities to be saved.
     */
    public void saveResponses(List<Answer> answers) {
        answerRepo.saveAll(answers);
    }

    /**
     * Helper method to save answers for each question in the survey.
     * @param submission - The Submission entity to associate answers with.
     * @param surveyId - The ID of the survey being submitted.
     * @param answers - Map of answers, where the key is the question ID and the value is the answer text.
     */
    public void saveAnswers(Submission submission, UUID surveyId, Map<UUID, String> answers) {
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
            saveResponses(List.of(answer));
        });
    }

    /**
     * Retrieves all answers associated with a given survey ID.
     *
     * @param surveyId The UUID of the survey whose answers are to be retrieved.
     * @return A list of Answer entities linked to the given survey ID.
     */
    public List<Answer> getAnswersBySurveyId(UUID surveyId) {
        return answerRepo.findAllBySurveyId(surveyId);
    }
}
