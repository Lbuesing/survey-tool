package com.example.simple_survey_tool.repository_layer;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.simple_survey_tool.model_layer.Answer;

public interface AnswerRepo extends JpaRepository<Answer, UUID>{
    @Query("SELECT a FROM Answer a WHERE a.submission.survey.id = :surveyId")
    List<Answer> findAllBySurveyId(@Param("surveyId") UUID surveyId);
}
