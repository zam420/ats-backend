package com.deepconnect.ats.jobapplication.dto;

import java.time.LocalDateTime;

import com.deepconnect.ats.job.dto.JobPositionSummaryResForCandidate;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.tenant.service.TenantLogoStorage;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.Getter;

@Getter
public class JobApplicationSummaryRes {

    @JsonUnwrapped
    private final JobPositionSummaryResForCandidate job;
    private final String applicationId;
    private final LocalDateTime appliedAt;
    private final boolean canViewJob;

    public JobApplicationSummaryRes(JobApplication application, String publicLogoBaseUrl) {
        JobPosition position = application.getJobPosition();
        this.job = JobPositionSummaryResForCandidate.from(position);
        this.job.setCompanyLogoUrl(TenantLogoStorage.publicUrl(position.getTenant().getLogoPath(), publicLogoBaseUrl));
        this.applicationId = application.getPublicId();
        this.appliedAt = application.getCreatedAt();
        this.canViewJob = !Boolean.TRUE.equals(position.getDeactivated()) && position.getStatus() == JobStatus.OPEN;
    }
    
}
