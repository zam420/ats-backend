package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.dto.JobPositionSearchQuery;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.enums.JobPostedDateType;
import com.deepconnect.ats.job.model.JobPosition;
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
