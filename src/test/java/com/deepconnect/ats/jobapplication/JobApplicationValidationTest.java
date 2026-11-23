package com.deepconnect.ats.jobapplication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.deepconnect.ats.jobapplication.dto.JobApplicationReq;
import com.deepconnect.ats.jobapplication.model.JobApplication;

class JobApplicationValidationTest {
    @Test
    void validatesEntityRequiredFieldsAndOptionalRejectionDetails() {
        try (var validator = validator()) {
            JobApplication application = new JobApplication();
            assertFalse(validator.validateProperty(application, "jobPositionId").isEmpty());
            assertFalse(validator.validateProperty(application, "candidateUserId").isEmpty());
            assertFalse(validator.validateProperty(application, "resumeName").isEmpty());
            assertTrue(validator.validateProperty(application, "recruitmentPipelineStage").isEmpty());
            assertTrue(validator.validateProperty(application, "preferredName").isEmpty());
            assertTrue(validator.validateProperty(application, "closedReason").isEmpty());

            application.setClosedReason("x".repeat(2000));
            assertTrue(validator.validateProperty(application, "closedReason").isEmpty());
            application.setClosedReason("x".repeat(2001));
            assertFalse(validator.validateProperty(application, "closedReason").isEmpty());
        }
    }

    @Test
    void validatesRequestAndNestedAnswers() {
        try (var validator = validator()) {
            JobApplicationReq request = new JobApplicationReq();
            request.setFirstName("A".repeat(100));
            request.setLastName("Name");
            request.setEmail("candidate@example.com");
            request.setCountryCode("+1");
            request.setPhoneNumber("123");
            request.setCurrentLocation("City");
            request.setAnswers(List.of());
            assertTrue(validator.validate(request).isEmpty());

            request.setFirstName("A".repeat(101));
            request.setEmail("not-an-email");
            request.setAnswers(Arrays.asList((JobApplicationReq.Answer) null));
            Set<String> paths = validator.validate(request).stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .collect(Collectors.toSet());
            assertTrue(paths.contains("firstName"));
            assertTrue(paths.contains("email"));
            assertTrue(paths.stream().anyMatch(path -> path.startsWith("answers")));

            JobApplicationReq.Answer answer = new JobApplicationReq.Answer();
            answer.setQuestionId("question");
            answer.setValues(List.of(" "));
            request.setAnswers(List.of(answer));
            assertTrue(validator.validate(request).stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().contains("values")));
        }
    }

    @Test
    void interpolatesLocalizedFieldLabelsAndLimits() {
        try (var validator = validator()) {
            JobApplicationReq request = new JobApplicationReq();
            request.setFirstName("A".repeat(101));
            try {
                LocaleContextHolder.setLocale(Locale.US);
                assertEquals("First name cannot be more than 100 characters.",
                        validator.validateProperty(request, "firstName").iterator().next().getMessage());
                LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
                assertEquals("名字不能超过100个字符。",
                        validator.validateProperty(request, "firstName").iterator().next().getMessage());
            } finally {
                LocaleContextHolder.resetLocaleContext();
            }
        }
    }

    private LocalValidatorFactoryBean validator() {
        ResourceBundleMessageSource messages = new ResourceBundleMessageSource();
        messages.setBasenames("localization/validation/validation", "localization/field/field");
        messages.setDefaultEncoding("UTF-8");
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(messages);
        validator.afterPropertiesSet();
        return validator;
    }
}
