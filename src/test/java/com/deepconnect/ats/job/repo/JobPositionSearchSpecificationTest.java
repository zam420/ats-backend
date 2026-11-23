package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.dto.JobPositionSearchQuery;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.enums.JobPostedDateType;
import com.deepconnect.ats.job.enums.JobEmploymentType;
import com.deepconnect.ats.job.enums.JobWorkArrangementType;
import com.deepconnect.ats.job.enums.JobExperienceLevelType;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.jobapplication.dto.JobApplicationSearchQuery;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class JobPositionSearchSpecificationTest {
    @Test
    void applicationSearchUsesEnumValuesForJobFilters() {
        JobApplicationSearchQuery search = new JobApplicationSearchQuery();
        search.setEmploymentTypes(List.of(0));
        search.setWorkArrangementTypes(List.of(1));
        search.setExperienceLevelTypes(List.of(2));
        Root<JobApplication> root = mock(Root.class);
        Join<JobApplication, JobPosition> position = mock(Join.class);
        Path<JobEmploymentType> employment = mock(Path.class);
        Path<JobWorkArrangementType> arrangement = mock(Path.class);
        Path<JobExperienceLevelType> experience = mock(Path.class);
        HibernateCriteriaBuilder cb = mock(HibernateCriteriaBuilder.class);
        when(root.<JobApplication, JobPosition>join("jobPosition")).thenReturn(position);
        when(position.<JobEmploymentType>get("employmentType")).thenReturn(employment);
        when(position.<JobWorkArrangementType>get("workArrangementType")).thenReturn(arrangement);
        when(position.<JobExperienceLevelType>get("experienceLevelType")).thenReturn(experience);

        JobPositionSearchSpecification.forApplications(search, 42L).toPredicate(root, null, cb);

        verify(employment).in(List.of(JobEmploymentType.FULL_TIME));
        verify(arrangement).in(List.of(JobWorkArrangementType.REMOTE));
        verify(experience).in(List.of(JobExperienceLevelType.SENIOR));
    }

    @Test
    void applicationSearchScopesCandidateAndFiltersAppliedDateWithoutExcludingClosedJobs() {
        JobApplicationSearchQuery search = new JobApplicationSearchQuery();
        search.setJobAppliedDateType(1);
        Root<JobApplication> root = mock(Root.class);
        Join<JobApplication, JobPosition> position = mock(Join.class);
        Path<Long> candidateId = mock(Path.class);
        Path<LocalDateTime> appliedAt = mock(Path.class);
        HibernateCriteriaBuilder cb = mock(HibernateCriteriaBuilder.class);
        when(root.<JobApplication, JobPosition>join("jobPosition")).thenReturn(position);
        when(root.<Long>get("candidateUserId")).thenReturn(candidateId);
        when(root.<LocalDateTime>get("createdAt")).thenReturn(appliedAt);

        JobPositionSearchSpecification.forApplications(search, 42L).toPredicate(root, null, cb);

        verify(cb).equal(candidateId, 42L);
        verify(cb).between(eq(appliedAt), any(LocalDateTime.class), any(LocalDateTime.class));
        verify(position, never()).get("status");
        verify(position, never()).get("deactivated");
    }

    @Test
    void datePostedCutoffsUseRollingPeriods() {
        LocalDateTime now = LocalDateTime.of(2025, 3, 31, 12, 0);
        assertEquals(now.minusDays(1), JobPostedDateType.ONE_DAY.cutoff(now));
        assertEquals(now.minusWeeks(1), JobPostedDateType.ONE_WEEK.cutoff(now));
        assertEquals(now.minusWeeks(2), JobPostedDateType.TWO_WEEKS.cutoff(now));
        assertEquals(LocalDateTime.of(2025, 2, 28, 12, 0), JobPostedDateType.ONE_MONTH.cutoff(now));
    }

    @Test
    void filtersBySelectedStatusesIncludingDraft() {
        JobPositionSearchQuery search = new JobPositionSearchQuery();
        search.setJobStatuses(List.of(0, 2));
        Root<JobPosition> root = mock(Root.class);
        Path<JobStatus> status = mock(Path.class);
        HibernateCriteriaBuilder cb = mock(HibernateCriteriaBuilder.class);
        when(root.<JobStatus>get("status")).thenReturn(status);

        JobPositionSearchSpecification.forTenantUser(search, 1L, 2L, "user", true)
            .toPredicate(root, null, cb);

        verify(status).in(List.of(JobStatus.DRAFT, JobStatus.CLOSED));
    }

    @Test
    void leavesStatusUnfilteredWhenNoneSelected() {
        JobPositionSearchQuery search = new JobPositionSearchQuery();
        search.setJobStatuses(List.of());
        Root<JobPosition> root = mock(Root.class);
        HibernateCriteriaBuilder cb = mock(HibernateCriteriaBuilder.class);

        JobPositionSearchSpecification.forTenantUser(search, 1L, 2L, "user", true)
            .toPredicate(root, null, cb);

        verify(root, never()).get("status");
    }
}
