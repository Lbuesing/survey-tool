package com.example.simple_survey_tool.model_layer;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.UUID;

public class SubmitAnswersRequest {

    @JsonProperty("surveyId")
    private String surveyId;

    @JsonProperty("answers")
    private Map<UUID, String> answers;

    public UUID getSurveyId() {
        return UUID.fromString(surveyId);
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public Map<UUID, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<UUID, String> answers) {
        this.answers = answers;
    }
}