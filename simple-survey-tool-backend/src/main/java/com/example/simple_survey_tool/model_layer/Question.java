package com.example.simple_survey_tool.model_layer;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "survey_id")
    // private Survey survey;
    @JsonProperty("survey_id")
    private UUID survey_id;
    @JsonProperty("text")
    private String text;


    @JsonIgnore
    @Column(name = "response_options")
    private String responseOptionsSerialized;

    @Transient
    @JsonProperty("responseOptions")
    private List<String> responseOptions;


    public Question(String text) {
        this.text = text;
    }
    public Question(Survey survey,String text) {
        this.survey_id = survey.getId();
        this.text = text;
    }

    // Lifecycle hooks to convert list <-> string
    @jakarta.persistence.PrePersist
    @jakarta.persistence.PreUpdate
    public void serializeOptions() {
        if (responseOptions != null) {
            responseOptionsSerialized = String.join(";;", responseOptions);
        }
    }

    @jakarta.persistence.PostLoad
    public void deserializeOptions() {
        if (responseOptionsSerialized != null && !responseOptionsSerialized.isEmpty()) {
            responseOptions = Arrays.asList(responseOptionsSerialized.split(";;"));
        }
    }
    

}
