package com.example.simple_survey_tool.controller_layer;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.simple_survey_tool.model_layer.Survey;
import com.example.simple_survey_tool.service_layer.SurveyService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
@Validated
public class SurveyController {
    private final SurveyService surveyService;

    /**
     * This method is called when a POST request is made
     * URL: localhost:8080/api/surveys
     * Purpose: Save an Survey entity
     * @param survey - Request body is an Survey entity
     * @return Saved Survey entity
     */
    @PostMapping("/")
    public ResponseEntity<Survey> saveSurvey(@RequestBody Survey survey)
    {
        return ResponseEntity.ok().body(surveyService.saveSurvey(survey));
    }
    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/api/surveys/1 (or any other id)
     * Purpose: Fetches Survey with the given id
     * @param id - survey id
     * @return Survey with the given id
     */
    @GetMapping("/{id}")
    @CrossOrigin(origins = "*")
    public ResponseEntity<Survey> getSurveyById(@PathVariable UUID id)
    {
        try {
            Survey survey = surveyService.getSurveyById(id);
            return ResponseEntity.ok(survey);
        } catch (EntityNotFoundException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}
