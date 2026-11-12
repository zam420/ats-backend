package com.deepconnect.ats.screeningquestionsform.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import jakarta.validation.Validation;
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
        assertFalse(mapper.valueToTree(response).has("name"));
    }

    @Test
    void validatesQuestionLabelAndDescriptionLengths() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            ScreeningQuestion question = new ScreeningQuestion();
            question.setLabel("L".repeat(100));
            question.setDescription("D".repeat(200));
            assertTrue(validator.validateProperty(question, "label").isEmpty());
            assertTrue(validator.validateProperty(question, "description").isEmpty());
            question.setLabel("L".repeat(101));
            question.setDescription("D".repeat(201));
            assertFalse(validator.validateProperty(question, "label").isEmpty());
            assertFalse(validator.validateProperty(question, "description").isEmpty());
        }
    }

    @Test
    void headingLevelRoundTripsAndIsValidated() throws Exception {
        ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        ScreeningQuestion question = mapper.readValue("{\"heading_level\":3}", ScreeningQuestion.class);
        assertEquals(3, question.getHeadingLevel());
        assertEquals(3, mapper.valueToTree(ScreeningQuestionRes.from(question)).get("heading_level").asInt());
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            for (int level = 1; level <= 3; level++) {
                question.setHeadingLevel(level);
                assertTrue(validator.validateProperty(question, "headingLevel").isEmpty());
            }
            question.setHeadingLevel(0);
            assertFalse(validator.validateProperty(question, "headingLevel").isEmpty());
            question.setHeadingLevel(4);
            assertFalse(validator.validateProperty(question, "headingLevel").isEmpty());
            question.setHeadingLevel(null);
            assertTrue(validator.validateProperty(question, "headingLevel").isEmpty());
        }
    }
}
