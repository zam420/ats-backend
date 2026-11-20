package com.deepconnect.ats.job.dto;

import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.enums.JobPostedDateType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class JobPositionSearchQuery extends AtsPageQuery {
    @Size(max = 100)
    private String title;
    private List<@NotNull Integer> workLocations;
    private List<@NotNull Integer> employmentTypes;
    private List<@NotNull Integer> workArrangementTypes;
    private List<@NotNull Integer> experienceLevelTypes;
    private List<@NotNull Integer> jobStatuses;
    private Integer jobPostedDateType;
    @Size(max = 12)
    private String companyId;

    @AssertTrue(message = "{validation.invalidJobPostedDateType}")
    public boolean isValidJobPostedDateType() {
        return jobPostedDateType == null || Arrays.stream(JobPostedDateType.values())
            .anyMatch(type -> type.getId().equals(jobPostedDateType));
    }

    @AssertTrue(message = "{validation.invalidJobStatus}")
    public boolean isValidJobStatuses() {
        return jobStatuses == null || jobStatuses.stream().allMatch(id ->
            id != null && Arrays.stream(JobStatus.values()).anyMatch(status -> status.getId().equals(id)));
    }
}
