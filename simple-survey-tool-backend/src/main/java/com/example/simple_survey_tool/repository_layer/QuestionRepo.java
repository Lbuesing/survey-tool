package com.example.simple_survey_tool.repository_layer;

import com.example.simple_survey_tool.model_layer.Question;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface QuestionRepo extends JpaRepository<Question, UUID>{
    @Query("SELECT q FROM Question q WHERE q.survey_id = :surveyId")
    List<Question> findBySurveyIdCustom(UUID surveyId);
}