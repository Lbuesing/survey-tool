package com.example.simple_survey_tool.repository_layer;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.simple_survey_tool.model_layer.Submission;
import java.util.UUID;

public interface SubmissionRepo extends JpaRepository<Submission, UUID> {
}