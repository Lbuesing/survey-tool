package com.example.simple_survey_tool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;

@SpringBootApplication
public class SimpleSurveyToolApplication {

	public static void main(String[] args) {
		SpringApplication.run(SimpleSurveyToolApplication.class, args);
	}

}
