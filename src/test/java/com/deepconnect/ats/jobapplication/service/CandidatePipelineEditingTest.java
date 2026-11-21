package com.deepconnect.ats.jobapplication.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.service.EnumsResService;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobEvaluationPlanRoundRepository;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.repo.JobScreeningQuestionRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.job.service.JobPositionService;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;
import com.deepconnect.ats.user.service.UserServiceV2;

class CandidatePipelineEditingTest {
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobRecruitmentPipelineRoundRepository stages = mock(JobRecruitmentPipelineRoundRepository.class);
    private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
    private final JobPositionService service = new JobPositionService(mock(EvaluationPlanService.class),
            mock(RecruitmentPipelineService.class), mock(JobPositionAccessService.class),
            mock(ScreeningQuestionsFormService.class), mock(UserServiceV2.class), mock(EnumsResService.class),
            positions, mock(JobEvaluationPlanRoundRepository.class), stages,
            mock(JobScreeningQuestionRepository.class), applications);
    private final JobPosition job = new JobPosition();
    private final JobPositionReq request = new JobPositionReq();
    private final JobRecruitmentPipelineRound applied = stage(10L, "applied", RecruitmentPipelineRoundType.APPLIED, 0, "Applied");
    private final JobRecruitmentPipelineRound screening = stage(11L, "screening", RecruitmentPipelineRoundType.SCREENING, 1, "Screening");

    @BeforeEach
    void setUp() {
        var user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        ReflectionTestUtils.setField(service, "maxRounds", 10);
        ReflectionTestUtils.setField(service, "shortDescriptionMaxWords", 70);
        job.setId(5L);
        job.setPublicId("job");
        job.setRecruitmentPipelineRounds(new ArrayList<>(List.of(applied, screening)));
        job.setEvaluationPlanRounds(List.of());
        job.setScreeningQuestions(List.of());
        request.setId("job");
        request.setDescription("Description");
        request.setEvaluationPlanRounds(List.of());
        request.setScreeningQuestions(List.of());
        when(positions.findForUpdate("job", 2L)).thenReturn(Optional.of(job));
        when(positions.save(any(JobPosition.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(applied, screening));
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private JobRecruitmentPipelineRound stage(Long id, String publicId, RecruitmentPipelineRoundType type, int order, String name) {
        var round = new JobRecruitmentPipelineRound();
        round.setId(id);
        round.setPublicId(publicId);
        round.setRoundType(type);
        round.setOrder(order);
        round.setName(name);
        return round;
    }

    @Test
    void preventsRemovingAnOccupiedStage() {
        request.setRecruitmentPipelineRounds(List.of(stage(null, "applied", RecruitmentPipelineRoundType.APPLIED, 0, "Applied")));
        when(applications.existsByRecruitmentPipelineStageAndRejectedFalse(11L)).thenReturn(true);
        CustomException error = assertThrows(CustomException.class, () -> service.update(request));
        assertEquals(CustomException.getFullMessageKey(CustomExceptionType.JOB_PIPELINE_OCCUPIED_STAGE), error.getMessageKey());
        verify(stages, never()).saveAllAndFlush(any());
    }

    @Test
    void preservesOccupiedStageIdentityWhenRenamed() {
        request.setRecruitmentPipelineRounds(List.of(
                stage(null, "applied", RecruitmentPipelineRoundType.APPLIED, 0, "Applied"),
                stage(null, "screening", RecruitmentPipelineRoundType.SCREENING, 1, "Review")));
        service.update(request);
        assertEquals(11L, screening.getId());
        assertEquals("Review", screening.getName());
        assertEquals(1, screening.getOrder());
        assertTrue(job.getRecruitmentPipelineRounds().contains(screening));
    }

    @Test
    void clearsRejectedReferencesBeforeRemovingAnEmptyStage() {
        request.setRecruitmentPipelineRounds(List.of(stage(null, "applied", RecruitmentPipelineRoundType.APPLIED, 0, "Applied")));
        service.update(request);
        verify(applications).clearRejectedStages(List.of(11L));
        assertFalse(job.getRecruitmentPipelineRounds().contains(screening));
    }
}
