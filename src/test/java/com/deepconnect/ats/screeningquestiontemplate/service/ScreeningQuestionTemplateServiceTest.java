package com.deepconnect.ats.screeningquestiontemplate.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;

class ScreeningQuestionTemplateServiceTest {
    private final ScreeningQuestionsFormService service =
            new ScreeningQuestionsFormService(null, null, null, null, null, null);

    private ScreeningQuestion question() {
        return ScreeningQuestion.builder()
                .order(1)
                .screeningQuestionTypeId(2)
                .label("Label")
                .description("Description")
                .options(new ArrayList<>(List.of("First", "Second")))
                .isMandatory(true)
                .isDropDownMultiSelect(true)
                .maxChars(50)
                .build();
    }

    private boolean equal(List<ScreeningQuestion> existing, List<ScreeningQuestion> incoming) {
        return Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(service, "questionsEqual", existing, incoming));
    }

    @Test
    void builderIncludesInheritedAndQuestionFieldsAndPreservesDefaults() {
        ScreeningQuestion question = ScreeningQuestion.builder()
                .id(42L)
                .label("Label")
                .screeningQuestionsFormId(12L)
                .build();
        assertEquals(42L, question.getId());
        assertEquals("Label", question.getLabel());
        assertEquals(12L, question.getScreeningQuestionsFormId());
        assertFalse(question.getIsMandatory());
        assertFalse(question.getIsDropDownMultiSelect());
        assertFalse(new ScreeningQuestion().getIsMandatory());
        assertFalse(new ScreeningQuestion().getIsDropDownMultiSelect());
    }

    @Test
    void equalQuestionsIncludeMatchingOptionsAndFlags() {
        assertTrue(equal(List.of(question()), List.of(question())));
        assertTrue(equal(null, List.of()));
        ScreeningQuestion first = question();
        ScreeningQuestion second = question();
        first.setOptions(null);
        second.setOptions(null);
        first.setIsMandatory(null);
        second.setIsMandatory(null);
        first.setIsDropDownMultiSelect(null);
        second.setIsDropDownMultiSelect(null);
        first.setMaxChars(null);
        second.setMaxChars(null);
        assertTrue(equal(List.of(first), List.of(second)));
    }

    static Stream<Consumer<ScreeningQuestion>> changedFields() {
        return Stream.of(
                question -> question.setOptions(List.of("First", "Changed")),
                question -> question.setOptions(List.of("Second", "First")),
                question -> question.setOptions(null),
                question -> question.setIsMandatory(false),
                question -> question.setIsMandatory(null),
                question -> question.setIsDropDownMultiSelect(false),
                question -> question.setIsDropDownMultiSelect(null),
                question -> question.setMaxChars(51),
                question -> question.setMaxChars(null));
    }

    @ParameterizedTest
    @MethodSource("changedFields")
    void changedFieldMakesQuestionsUnequal(Consumer<ScreeningQuestion> change) {
        ScreeningQuestion incoming = question();
        change.accept(incoming);
        assertFalse(equal(List.of(question()), List.of(incoming)));
    }
}
