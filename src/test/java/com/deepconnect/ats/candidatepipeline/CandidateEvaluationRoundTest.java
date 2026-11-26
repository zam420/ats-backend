package com.deepconnect.ats.candidatepipeline;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;

import jakarta.validation.Validation;

class CandidateEvaluationRoundTest {
    @Test
    void completedStatusAndResultMustBeSetTogether() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            var round = new CandidateEvaluationRound();
            assertTrue(validator.validateProperty(round, "resultConsistent").isEmpty());

            round.setStatus(CandidateEvaluationStatus.COMPLETED);
            assertFalse(validator.validateProperty(round, "resultConsistent").isEmpty());
            round.setResult(CandidateEvaluationResult.STRONG_NO);
            assertTrue(validator.validateProperty(round, "resultConsistent").isEmpty());

            round.setStatus(CandidateEvaluationStatus.SCHEDULED);
            assertFalse(validator.validateProperty(round, "resultConsistent").isEmpty());
        }
    }
}
