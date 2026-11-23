package com.deepconnect.ats.jobapplication.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.tenant.model.Tenant;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

class JobApplicationSummaryResTest {
    @Test
    void returnsApplicationDateAndDisablesUnavailableJobs() {
        Tenant tenant = new Tenant();
        tenant.setName("Company");
        JobPosition job = new JobPosition();
        job.setTenant(tenant);
        job.setPublicId("job1");
        job.setTitle("Engineer");
        job.setDeactivated(false);
        job.setStatus(JobStatus.OPEN);
        JobApplication application = new JobApplication();
        application.setPublicId("application1");
        application.setJobPosition(job);
        application.setCreatedAt(LocalDateTime.of(2025, 1, 2, 12, 0));

        var response = new JobApplicationSummaryRes(application, "");
        assertTrue(response.isCanViewJob());
        var json = new ObjectMapper().findAndRegisterModules()
                .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE).valueToTree(response);
        assertEquals("job1", json.get("id").asText());
        assertEquals("application1", json.get("application_id").asText());
        assertTrue(json.hasNonNull("applied_at"));

        job.setStatus(JobStatus.CLOSED);
        assertFalse(new JobApplicationSummaryRes(application, "").isCanViewJob());
        job.setStatus(JobStatus.OPEN);
        job.setDeactivated(true);
        assertFalse(new JobApplicationSummaryRes(application, "").isCanViewJob());
    }
}
