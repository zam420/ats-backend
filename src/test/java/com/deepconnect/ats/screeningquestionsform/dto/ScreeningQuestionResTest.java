package com.deepconnect.ats.screeningquestionsform.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import org.junit.jupiter.api.Test;

class ScreeningQuestionResTest {
    @Test
    void includesDefaultOptionInSnakeCaseResponse() {
        ScreeningQuestion question = new ScreeningQuestion();
        question.setDefaultOption("Choice A");

        ScreeningQuestionRes response = ScreeningQuestionRes.from(question);
        assertEquals("Choice A", response.getDefaultOption());
        ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        assertEquals("Choice A", mapper.valueToTree(response).get("default_option").asText());
    }
}
