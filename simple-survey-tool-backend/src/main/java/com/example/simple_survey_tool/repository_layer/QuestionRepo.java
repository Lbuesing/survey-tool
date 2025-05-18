package com.example.simple_survey_tool.repository_layer;

import com.example.simple_survey_tool.model_layer.Question;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepo extends JpaRepository<Question, UUID>{

}