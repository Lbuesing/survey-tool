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
 * Service layer is where all the business logic lies
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyService {
    private final SurveyRepo surveyRepo;
    private final QuestionRepo questionRepo;

    public List<Survey> getAllSurveys(){
        return surveyRepo.findAll();
    }

    public Survey getSurveyById(UUID id){
        return surveyRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Survey with id " + id + " not found"));
    }

    public Survey saveSurvey(Survey survey) {
    survey.setCreatedAt(LocalDateTime.now());
    survey.setUpdatedAt(LocalDateTime.now());
    survey.setResponse_count(0);
    Survey savedSurvey = surveyRepo.save(survey);
    log.info("Survey with id: {} saved successfully", savedSurvey.getId());

    List<Question> savedQuestions = new ArrayList<>();
    List<String> defaultLikertScale = List.of(
        "Totally disagree",
        "Disagree",
        "Neutral",
        "Agree",
        "Fully Agree"
    );

    for (Question question : survey.getQuestions()) {
        // If responseOptions are null or empty, assign default Likert scale
        if (question.getResponseOptions() == null || question.getResponseOptions().isEmpty()) {
            question.setResponseOptions(defaultLikertScale);
        }

        // Recreate the question to ensure survey_id is correctly set
        Question newQuestion = new Question(savedSurvey, question.getText());
        newQuestion.setResponseOptions(question.getResponseOptions()); // preserve/assign options
        Question savedQuestion = questionRepo.save(newQuestion);

        log.info("Question with id: {} and survey_id: {} saved successfully", 
                 savedQuestion.getId(), savedQuestion.getSurvey_id());

        savedQuestions.add(savedQuestion);
    }

    savedSurvey.setQuestions(savedQuestions);
    return savedSurvey;
}


    public void deleteSurveyById (UUID id) {
        surveyRepo.deleteById(id);
    }
}
