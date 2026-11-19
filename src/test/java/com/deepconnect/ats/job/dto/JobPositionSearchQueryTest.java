package com.deepconnect.ats.job.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JobPositionSearchQueryTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void allowsInitialUnfilteredList() {
        assertTrue(validator.validate(new JobPositionSearchQuery()).isEmpty());
    }

    @Test
    void allowsSearchWithoutTitle() {
        JobPositionSearchQuery query = new JobPositionSearchQuery();
        query.setTitle("   ");
        assertTrue(validator.validate(query).isEmpty());

        query.setTitle(null);
        query.setWorkLocations(List.of(0));
        assertTrue(validator.validate(query).isEmpty());

        query.setTitle("Software Engineer");
        query.setEmploymentTypes(List.of(1, 2));
        assertTrue(validator.validate(query).isEmpty());
    }

    @Test
    void rejectsTitlesLongerThanTheColumn() {
        JobPositionSearchQuery query = new JobPositionSearchQuery();
        query.setTitle("a".repeat(101));
        assertFalse(validator.validate(query).isEmpty());
    }

    @Test
    void acceptsValidStatusesAndRejectsUnknownOnes() {
        JobPositionSearchQuery query = new JobPositionSearchQuery();
        query.setJobStatuses(List.of(0, 2));
        assertTrue(validator.validate(query).isEmpty());
        query.setJobStatuses(List.of(0, 99));
        assertFalse(validator.validate(query).isEmpty());
    }

    @Test
    void acceptsDatePostedIdsIncludingZeroAndRejectsUnknownIds() {
        JobPositionSearchQuery query = new JobPositionSearchQuery();
        for (int id = 0; id <= 3; id++) {
            query.setJobPostedDateType(id);
            assertTrue(validator.validate(query).isEmpty());
        }
        query.setJobPostedDateType(4);
        assertFalse(validator.validate(query).isEmpty());
    }
}
