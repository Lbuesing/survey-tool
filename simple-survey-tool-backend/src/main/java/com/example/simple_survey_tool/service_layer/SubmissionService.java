package com.example.simple_survey_tool.service_layer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.simple_survey_tool.model_layer.Answer;
import com.example.simple_survey_tool.model_layer.Submission;
import com.example.simple_survey_tool.model_layer.Survey;
import com.example.simple_survey_tool.repository_layer.AnswerRepo;
import com.example.simple_survey_tool.repository_layer.SubmissionRepo;

/**
 * Service class that handles business logic related to survey submissions and responses.
 */
@Service
public class SubmissionService {
    private final SubmissionRepo submissionRepo;
    private final AnswerRepo answerRepo;

    /**
     * Constructor for SubmissionService.
     *
     * @param submissionRepo Repository for handling submission data.
     * @param answerRepo Repository for handling answer data.
     */
    public SubmissionService(SubmissionRepo submissionRepo, AnswerRepo answerRepo) {
        this.submissionRepo = submissionRepo;
        this.answerRepo = answerRepo;
    }

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
     * Retrieves all answers associated with a given survey ID.
     *
     * @param surveyId The UUID of the survey whose answers are to be retrieved.
     * @return A list of Answer entities linked to the given survey ID.
     */
    public List<Answer> getAnswersBySurveyId(UUID surveyId) {
        return answerRepo.findAllBySurveyId(surveyId);
    }
}
