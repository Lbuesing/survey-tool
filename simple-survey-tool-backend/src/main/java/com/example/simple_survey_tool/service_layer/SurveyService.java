package com.example.simple_survey_tool.service_layer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.simple_survey_tool.model_layer.Question;
import com.example.simple_survey_tool.model_layer.Survey;
import com.example.simple_survey_tool.repository_layer.QuestionRepo;
import com.example.simple_survey_tool.repository_layer.SurveyRepo;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer class responsible for business logic related to surveys and their questions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyService {

    private final SurveyRepo surveyRepo;
    private final QuestionRepo questionRepo;

    /**
     * Retrieves all surveys from the repository.
     *
     * @return List of all surveys.
     */
    public List<Survey> getAllSurveys() {
        return surveyRepo.findAll();
    }

    /**
     * Fetches a survey by its unique ID.
     *
     * @param id UUID of the survey.
     * @return Survey object if found.
     * @throws EntityNotFoundException if the survey is not found.
     */
    public Survey getSurveyById(UUID id) {
        return surveyRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Survey with id " + id + " not found"));
    }

    /**
     * Saves a new survey and its associated questions.
     * 
     * - Assigns default Likert-scale options if none are provided.
     * - Recreates question objects to ensure proper survey linkage before saving.
     * - Automatically sets timestamps and response count for the new survey.
     *
     * @param survey Survey object to be saved.
     * @return The saved Survey with updated question references.
     */
    public Survey saveSurvey(Survey survey) {
        survey.setCreatedAt(LocalDateTime.now());
        survey.setUpdatedAt(LocalDateTime.now());
        survey.setResponse_count(0); // initialize response count

        // Save the survey first to get an ID for linking questions
        Survey savedSurvey = surveyRepo.save(survey);
        log.info("Survey with id: {} saved successfully", savedSurvey.getId());

        List<Question> savedQuestions = new ArrayList<>();

        // Define default Likert scale options
        List<String> defaultLikertScale = List.of(
                "Totally disagree",
                "Disagree",
                "Neutral",
                "Agree",
                "Fully Agree"
        );

        // Save each question and associate it with the saved survey
        for (Question question : survey.getQuestions()) {
            // Assign default options if none are provided
            if (question.getResponseOptions() == null || question.getResponseOptions().isEmpty()) {
                question.setResponseOptions(defaultLikertScale);
            }

            // Create a new question instance tied to the saved survey
            Question newQuestion = new Question(savedSurvey, question.getText());
            newQuestion.setResponseOptions(question.getResponseOptions());

            // Persist the question
            Question savedQuestion = questionRepo.save(newQuestion);
            log.info("Question with id: {} and survey_id: {} saved successfully",
                    savedQuestion.getId(), savedQuestion.getSurvey_id());

            savedQuestions.add(savedQuestion);
        }

        // Link the saved questions to the survey and return the full survey object
        savedSurvey.setQuestions(savedQuestions);
        return savedSurvey;
    }

    /**
     * Deletes a survey by its unique ID.
     *
     * @param id UUID of the survey to be deleted.
     */
    public void deleteSurveyById(UUID id) {
        surveyRepo.deleteById(id);
    }

    /**
     * Retrieves all questions for a specific survey by ID.
     *
     * @param surveyId UUID of the survey.
     * @return List of questions associated with the survey.
     */
    public List<Question> getQuestionsBySurveyId(UUID surveyId) {
        return questionRepo.findBySurveyIdCustom(surveyId);
    }

    /**
     * Increments the response count for a survey and updates its last updated timestamp.
     *
     * @param survey The survey object to update.
     */
    public void updateSurvey(Survey survey) {
        survey.setResponse_count(survey.getResponse_count() + 1); // increment response count
        survey.setUpdatedAt(LocalDateTime.now()); // update timestamp
        surveyRepo.save(survey);
    }
}
