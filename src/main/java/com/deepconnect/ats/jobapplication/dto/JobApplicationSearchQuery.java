package com.deepconnect.ats.jobapplication.dto;

import java.util.Arrays;

import com.deepconnect.ats.job.dto.JobPositionSearchQuery;
import com.deepconnect.ats.job.enums.JobPostedDateType;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobApplicationSearchQuery extends JobPositionSearchQuery {
    private Integer jobAppliedDateType;

    @AssertTrue(message = "{validation.invalidJobPostedDateType}")
    public boolean isValidJobAppliedDateType() {
        return jobAppliedDateType == null || Arrays.stream(JobPostedDateType.values())
                .anyMatch(type -> type.getId().equals(jobAppliedDateType));
    }
}
