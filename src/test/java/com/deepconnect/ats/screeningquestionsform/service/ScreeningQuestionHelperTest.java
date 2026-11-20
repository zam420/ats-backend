package com.deepconnect.ats.screeningquestionsform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionType;

class ScreeningQuestionHelperTest {
    @Test
    void sanitizesOnlyLabelControlsForBothQuestionTypes() {
        ScreeningQuestion label = new ScreeningQuestion();
        label.setScreeningQuestionTypeId(42);
        label.setLabel("<p>Hello <strong>world</strong><script>alert(1)</script></p>");
        JobScreeningQuestion jobLabel = new JobScreeningQuestion();
        jobLabel.setScreeningQuestionTypeId(42);
        jobLabel.setLabel("<p onclick=\"alert(1)\">Job label</p>");
        ScreeningQuestion input = new ScreeningQuestion();
        input.setScreeningQuestionTypeId(7);
        input.setLabel("<script>plain label</script>");
        List<ScreeningQuestionType> types = List.of(
                ScreeningQuestionType.builder().id(42).code("LBL").build(),
                ScreeningQuestionType.builder().id(7).code("INPUT").build());

        ScreeningQuestionHelper.sanitizeLabels(List.of(label, jobLabel, input), types);

        assertEquals("<p>Hello <strong>world</strong></p>", label.getLabel());
        assertEquals("<p>Job label</p>", jobLabel.getLabel());
        assertEquals("<script>plain label</script>", input.getLabel());
    }

    @Test
    void checkboxAndRadioLabelsUseRichTextSanitizationAndLimits() {
        ScreeningQuestion checkbox = new ScreeningQuestion();
        checkbox.setScreeningQuestionTypeId(3);
        checkbox.setLabel("<p><strong>Choose</strong><script>alert(1)</script></p>");
        JobScreeningQuestion radio = new JobScreeningQuestion();
        radio.setScreeningQuestionTypeId(4);
        radio.setLabel("<p><a href=\"javascript:alert(1)\">Choice</a></p>");
        List<ScreeningQuestionType> types = List.of(
                ScreeningQuestionType.builder().id(3).code("CHK").build(),
                ScreeningQuestionType.builder().id(4).code("RDO").build());

        ScreeningQuestionHelper.assertMaxLength(List.of(checkbox, radio), types, 100, 1);
        ScreeningQuestionHelper.sanitizeLabels(List.of(checkbox, radio), types);

        assertEquals("<p><strong>Choose</strong></p>", checkbox.getLabel());
        assertEquals("<p><a>Choice</a></p>", radio.getLabel());
        assertThrows(IllegalArgumentException.class,
                () -> ScreeningQuestionHelper.assertMaxLength(List.of(checkbox), types, 5, 100));
    }
}
