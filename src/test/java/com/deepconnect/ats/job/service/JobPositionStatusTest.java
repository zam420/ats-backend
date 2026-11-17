package com.deepconnect.ats.job.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.ValidationException;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobExperienceLevelType;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobEmploymentTypeRepository;
import com.deepconnect.ats.job.repo.JobExperienceLevelTypeRepository;
import com.deepconnect.ats.job.repo.JobEvaluationPlanRoundRepository;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.repo.JobScreeningQuestionRepository;
import com.deepconnect.ats.job.repo.JobWorkArrangementTypeRepository;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;
import com.deepconnect.ats.user.service.UserServiceV2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class JobPositionStatusTest {
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobPositionAccessService access = mock(JobPositionAccessService.class);
    private final JobExperienceLevelTypeRepository experienceLevels = mock(JobExperienceLevelTypeRepository.class);
    private final JobPositionService service = new JobPositionService(
            mock(EvaluationPlanService.class), mock(RecruitmentPipelineService.class), access,
            mock(ScreeningQuestionsFormService.class), mock(UserServiceV2.class), positions,
            mock(JobEmploymentTypeRepository.class), mock(JobWorkArrangementTypeRepository.class),
            experienceLevels,
            mock(JobEvaluationPlanRoundRepository.class), mock(JobRecruitmentPipelineRoundRepository.class),
            mock(JobScreeningQuestionRepository.class));
    private final JobPosition position = new JobPosition();
    private final JobPositionReq req = new JobPositionReq();

    @BeforeEach
    void setUp() {
        LoggedInUserDetails user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        var evaluation = List.of(new JobEvaluationPlanRound());
        var recruitment = List.of(new JobRecruitmentPipelineRound());
        position.setEvaluationPlanRounds(evaluation);
        position.setRecruitmentPipelineRounds(recruitment);
        req.setId("job");
        req.setTitle("Edited title");
        req.setEvaluationPlanRounds(evaluation);
        req.setRecruitmentPipelineRounds(recruitment);
        when(positions.findForUpdate("job", 2L)).thenReturn(Optional.of(position));
        when(positions.save(any(JobPosition.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @AfterEach
    void clearAuthentication() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void filtersIncludeExperienceLevels() {
        var level = new JobExperienceLevelType(3, "Senior", null);
        when(experienceLevels.findAll()).thenReturn(List.of(level));
        assertEquals(List.of(level), service.getFiltersData().getExperienceLevelTypes());
    }

    @Test
    void publishAndCloseSaveEditsAndRecordActor() {
        service.publish(req.getId());
        assertEquals(JobStatus.OPEN, position.getStatus());
        assertEquals("Edited title", position.getTitle());
        assertEquals(1L, position.getPublishedByUserId());
        assertNotNull(position.getPublishedAt());
        var publishedAt = position.getPublishedAt();

        service.close(req.getId());
        assertEquals(JobStatus.CLOSED, position.getStatus());
        assertEquals(1L, position.getClosedByUserId());
        assertEquals(publishedAt, position.getPublishedAt());
        verify(access, times(2)).assertWriteAccess(any(), eq(position));
    }

    @Test
    void invalidTransitionAndMissingRoundsDoNotSave() {
        position.setStatus(JobStatus.CLOSED);
        assertThrows(CustomException.class, () -> service.publish(req.getId()));
        position.setStatus(JobStatus.DRAFT);
        assertThrows(CustomException.class, () -> service.close(req.getId()));
        req.setEvaluationPlanRounds(List.of());
        assertThrows(ValidationException.class, () -> service.publish(req.getId()));
        assertEquals(JobStatus.DRAFT, position.getStatus());
        verify(positions, never()).save(any());
    }

    @Test
    void deniedWriteAccessCannotChangeStatus() {
        doThrow(new IllegalStateException("denied")).when(access).assertWriteAccess(any(), eq(position));
        assertThrows(IllegalStateException.class, () -> service.publish(req.getId()));
        assertEquals(JobStatus.DRAFT, position.getStatus());
        verify(positions, never()).save(any());
    }

    @Test
    void unknownOrDifferentTenantJobCannotBeChanged() {
        when(positions.findForUpdate("job", 2L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.publish(req.getId()));
        verifyNoInteractions(access);
        verify(positions, never()).save(any());
    }
}
