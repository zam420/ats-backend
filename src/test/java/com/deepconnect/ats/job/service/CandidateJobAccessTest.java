package com.deepconnect.ats.job.service;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.utils.EnumsResUtils;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.repo.*;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;
import com.deepconnect.ats.tenant.model.Tenant;
import com.deepconnect.ats.user.service.UserServiceV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CandidateJobAccessTest {
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobPositionService service = new JobPositionService(
        mock(EvaluationPlanService.class), mock(RecruitmentPipelineService.class), mock(JobPositionAccessService.class),
        mock(ScreeningQuestionsFormService.class), mock(UserServiceV2.class), mock(EnumsResUtils.class),
        positions, mock(JobEmploymentTypeRepository.class), mock(JobWorkArrangementTypeRepository.class),
        mock(JobExperienceLevelTypeRepository.class), mock(JobEvaluationPlanRoundRepository.class),
        mock(JobRecruitmentPipelineRoundRepository.class), mock(JobScreeningQuestionRepository.class));

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsEmployerBrandingForOpenJob() {
        Tenant tenant = new Tenant();
        tenant.setName("Employer");
        tenant.setLogoPath("logo.png");
        JobPosition job = new JobPosition();
        job.setTenant(tenant);
        when(positions.findByPublicIdAndStatusAndDeactivatedFalse("job", JobStatus.OPEN)).thenReturn(Optional.of(job));

        var result = service.findByPublicIdForCandidate("job");
        assertEquals("Employer", result.getCompanyName());
        assertTrue(result.isCompanyHasLogo());
        assertEquals("logo.png", service.findLogoForCandidate("job"));
    }

    @Test
    void rejectsJobsNotAvailableAsOpen() {
        var user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        when(positions.findByPublicIdAndStatusAndDeactivatedFalse("job", JobStatus.OPEN)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findByPublicIdForCandidate("job"));
        assertThrows(RuntimeException.class, () -> service.findLogoForCandidate("job"));
    }
}
