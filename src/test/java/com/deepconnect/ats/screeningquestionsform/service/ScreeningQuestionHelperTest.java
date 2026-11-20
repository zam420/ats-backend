package com.deepconnect.ats.screeningquestionsform.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;

class ScreeningQuestionHelperTest {
    @Test
    void sanitizesOnlyLabelControlsForBothQuestionTypes() {
        ScreeningQuestion label = new ScreeningQuestion();
        label.setScreeningQuestionType(ScreeningQuestionType.LABEL);
        label.setLabel("<p>Hello <strong>world</strong><script>alert(1)</script></p>");
        JobScreeningQuestion jobLabel = new JobScreeningQuestion();
        jobLabel.setScreeningQuestionType(ScreeningQuestionType.LABEL);
        jobLabel.setLabel("<p onclick=\"alert(1)\">Job label</p>");
        ScreeningQuestion input = new ScreeningQuestion();
        input.setScreeningQuestionType(ScreeningQuestionType.INPUT_BOX);
        input.setLabel("<script>plain label</script>");

        ScreeningQuestionHelper.sanitizeLabels(List.of(label, jobLabel, input));

        assertEquals("<p>Hello <strong>world</strong></p>", label.getLabel());
        assertEquals("<p>Job label</p>", jobLabel.getLabel());
        assertEquals("<script>plain label</script>", input.getLabel());
    }

    @Test
    void checkboxAndRadioLabelsUseRichTextSanitizationAndLimits() {
        ScreeningQuestion checkbox = new ScreeningQuestion();
        checkbox.setScreeningQuestionType(ScreeningQuestionType.CHECKBOX);
        checkbox.setLabel("<p><strong>Choose</strong><script>alert(1)</script></p>");
        JobScreeningQuestion radio = new JobScreeningQuestion();
        radio.setScreeningQuestionType(ScreeningQuestionType.RADIO);
        radio.setLabel("<p><a href=\"javascript:alert(1)\">Choice</a></p>");

        ScreeningQuestionHelper.assertMaxLength(List.of(checkbox, radio), 100, 1);
        ScreeningQuestionHelper.sanitizeLabels(List.of(checkbox, radio));

        assertEquals("<p><strong>Choose</strong></p>", checkbox.getLabel());
        assertEquals("<p><a>Choice</a></p>", radio.getLabel());
        assertThrows(IllegalArgumentException.class,
                () -> ScreeningQuestionHelper.assertMaxLength(List.of(checkbox), 5, 100));
    }
}
