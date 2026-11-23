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
    private final JobApplicationService applicationService = mock(JobApplicationService.class);
    private final JobPositionService service = new JobPositionService(mock(EvaluationPlanService.class),
            mock(RecruitmentPipelineService.class), mock(JobPositionAccessService.class),
            mock(ScreeningQuestionsFormService.class), applicationService,
            mock(UserServiceV2.class), mock(EnumsResService.class),
            positions, mock(JobEvaluationPlanRoundRepository.class), stages,
            mock(JobScreeningQuestionRepository.class), applications);
    private final JobPosition job = new JobPosition();
    private final JobPositionReq request = new JobPositionReq();
    private final JobRecruitmentPipelineRound applied = stage(10L, "applied", RecruitmentPipelineRoundType.APPLIED, 0, "Applied");
    private final JobRecruitmentPipelineRound screening = stage(11L, "custom", RecruitmentPipelineRoundType.CUSTOM, 1, "Screening");
    private final List<JobRecruitmentPipelineRound> existing = existingStages();

    private List<JobRecruitmentPipelineRound> existingStages() {
        List<JobRecruitmentPipelineRound> rounds = new ArrayList<>(List.of(applied, screening));
        int order = 2;
        for (RecruitmentPipelineRoundType type : List.of(RecruitmentPipelineRoundType.SCREENING,
                RecruitmentPipelineRoundType.EVALUATING, RecruitmentPipelineRoundType.OFFERED,
                RecruitmentPipelineRoundType.HIRED, RecruitmentPipelineRoundType.ONBOARDED)) {
            rounds.add(stage(20L + order, type.name(), type, order, type.name()));
            order++;
        }
        return rounds;
    }

    private List<JobRecruitmentPipelineRound> requestStages(boolean includeCustom, String customName) {
        List<JobRecruitmentPipelineRound> rounds = new ArrayList<>();
        for (JobRecruitmentPipelineRound current : existing) {
            if (current == screening && !includeCustom) continue;
            rounds.add(stage(null, current.getPublicId(), current.getRoundType(), rounds.size(),
                    current == screening ? customName : current.getName()));
        }
        return rounds;
    }

    @BeforeEach
    void setUp() {
        var user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", false, false, List.of());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, List.of()));
        ReflectionTestUtils.setField(service, "maxRounds", 10);
        ReflectionTestUtils.setField(service, "shortDescriptionMaxWords", 70);
        job.setId(5L);
        job.setPublicId("job");
        job.setRecruitmentPipelineRounds(new ArrayList<>(existing));
        job.setEvaluationPlanRounds(List.of());
        job.setScreeningQuestions(List.of());
        request.setId("job");
        request.setDescription("Description");
        request.setEvaluationPlanRounds(List.of());
        request.setScreeningQuestions(List.of());
        when(positions.findForUpdate("job", 2L)).thenReturn(Optional.of(job));
        when(positions.save(any(JobPosition.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(existing);
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
        request.setRecruitmentPipelineRounds(requestStages(false, null));
        when(applicationService.existsByRecruitmentPipelineStageAndClosedFalse(11L)).thenReturn(true);
        CustomException error = assertThrows(CustomException.class, () -> service.update(request));
        assertEquals(CustomException.getFullMessageKey(CustomExceptionType.JOB_PIPELINE_OCCUPIED_STAGE), error.getMessageKey());
        verify(stages, never()).saveAllAndFlush(any());
    }

    @Test
    void preservesOccupiedStageIdentityWhenRenamed() {
        request.setRecruitmentPipelineRounds(requestStages(true, "Review"));
        service.update(request);
        assertEquals(11L, screening.getId());
        assertEquals("Review", screening.getName());
        assertEquals(1, screening.getOrder());
        assertTrue(job.getRecruitmentPipelineRounds().contains(screening));
    }

    @Test
    void clearsRejectedReferencesBeforeRemovingAnEmptyStage() {
        request.setRecruitmentPipelineRounds(requestStages(false, null));
        service.update(request);
        verify(applicationService).clearClosedStages(List.of(11L));
        assertFalse(job.getRecruitmentPipelineRounds().contains(screening));
    }
}
