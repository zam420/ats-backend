package com.deepconnect.ats.jobapplication.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
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
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationPlanImpact;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.service.EnumsResService;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRoundReqRes;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobEvaluationPlanRoundRepository;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.repo.JobScreeningQuestionRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.job.service.JobPositionService;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRoundReqRes;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;
import com.deepconnect.ats.user.service.UserServiceV2;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;

class CandidatePipelineEditingTest {
    private final JobPositionRepository positions = mock(JobPositionRepository.class);
    private final JobRecruitmentPipelineRoundRepository stages = mock(JobRecruitmentPipelineRoundRepository.class);
    private final JobApplicationRepository applications = mock(JobApplicationRepository.class);
    private final JobEvaluationPlanRoundRepository evaluationRounds = mock(JobEvaluationPlanRoundRepository.class);
    private final JobApplicationService applicationService = mock(JobApplicationService.class);
    private final CandidateEvaluationPlanImpact evaluationImpact = mock(CandidateEvaluationPlanImpact.class);
    private final JobPositionService service = new JobPositionService(mock(EvaluationPlanService.class),
            mock(RecruitmentPipelineService.class), mock(JobPositionAccessService.class),
            mock(ScreeningQuestionsFormService.class), applicationService,
            mock(UserServiceV2.class), mock(EnumsResService.class),
            positions, evaluationRounds, stages,
            mock(JobScreeningQuestionRepository.class), applications, evaluationImpact);
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

    private List<RecruitmentPipelineRoundReqRes> requestStages(boolean includeCustom, String customName) {
        List<JobRecruitmentPipelineRound> rounds = new ArrayList<>();
        for (JobRecruitmentPipelineRound current : existing) {
            if (current == screening && !includeCustom) continue;
            rounds.add(stage(null, current.getPublicId(), current.getRoundType(), rounds.size(),
                    current == screening ? customName : current.getName()));
        }
        return RecruitmentPipelineRoundReqRes.from(rounds);
    }

    @BeforeEach
    void setUp() {
        var user = new LoggedInUserDetails(2L, 1L, "user", "user@example.com", "user name", false, false, List.of());
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
        when(evaluationImpact.preview(any(), any())).thenReturn(new CandidateEvaluationPlanImpact.Preview("token", 0, 0));
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

    @Test
    void archivesOldEvaluationRoundsWhenPlanChanges() {
        var oldRound = new JobEvaluationPlanRound();
        oldRound.setName("Old round");
        oldRound.setPublicId("round-old");
        oldRound.setLineageId("lineage-old");
        oldRound.setRoundType(EvaluationPlanRoundType.HR);
        oldRound.setOrder(0);
        var newRound = new JobEvaluationPlanRound();
        newRound.setName("Updated round");
        newRound.setRoundType(EvaluationPlanRoundType.HR);
        newRound.setOrder(0);
        newRound.setPublicId("round-old");
        job.setEvaluationPlanRounds(List.of(oldRound));
        request.setEvaluationPlanRounds(List.of(EvaluationPlanRoundReqRes.from(newRound)));
        request.setRecruitmentPipelineRounds(requestStages(true, "Screening"));

        service.update(request);

        var order = inOrder(evaluationRounds);
        order.verify(evaluationRounds).archiveByJobPositionId(5L);
        order.verify(evaluationRounds).saveAll(argThat(savedRounds -> {
            var iterator = savedRounds.iterator();
            if (!iterator.hasNext()) return false;
            var savedRound = iterator.next();
            return !iterator.hasNext() && Boolean.TRUE.equals(savedRound.getActive())
                    && "lineage-old".equals(savedRound.getLineageId());
        }));
    }

    @Test
    void rejectsStaleEvaluationRoundIdentityBeforeArchiving() {
        var oldRound = new JobEvaluationPlanRound();
        oldRound.setPublicId("round-old");
        oldRound.setLineageId("lineage-old");
        oldRound.setName("Old round");
        oldRound.setRoundType(EvaluationPlanRoundType.HR);
        oldRound.setOrder(0);
        var incoming = new JobEvaluationPlanRound();
        incoming.setPublicId("another-job-round");
        incoming.setName("Updated round");
        incoming.setRoundType(EvaluationPlanRoundType.HR);
        incoming.setOrder(0);
        job.setEvaluationPlanRounds(List.of(oldRound));
        request.setEvaluationPlanRounds(List.of(EvaluationPlanRoundReqRes.from(incoming)));
        request.setRecruitmentPipelineRounds(requestStages(true, "Screening"));

        assertThrows(CustomException.class, () -> service.update(request));
        verify(evaluationRounds, never()).archiveByJobPositionId(anyLong());
    }

    @Test
    void rejectsDuplicateEvaluationRoundIdentity() {
        var oldRound = new JobEvaluationPlanRound();
        oldRound.setPublicId("round-old");
        oldRound.setLineageId("lineage-old");
        oldRound.setName("Old round");
        oldRound.setRoundType(EvaluationPlanRoundType.HR);
        oldRound.setOrder(0);
        var first = new JobEvaluationPlanRound();
        first.setPublicId("round-old");
        var second = new JobEvaluationPlanRound();
        second.setPublicId("round-old");
        job.setEvaluationPlanRounds(List.of(oldRound));
        request.setEvaluationPlanRounds(List.of(EvaluationPlanRoundReqRes.from(first), EvaluationPlanRoundReqRes.from(second)));
        request.setRecruitmentPipelineRounds(requestStages(true, "Screening"));

        assertThrows(CustomException.class, () -> service.update(request));
        verify(evaluationRounds, never()).archiveByJobPositionId(anyLong());
    }

    @Test
    void requiresFreshConfirmationBeforeChangingAnImpactedPlan() {
        var old = new JobEvaluationPlanRound();
        old.setPublicId("old");
        old.setLineageId("lineage");
        old.setName("Old");
        old.setOrder(0);
        old.setRoundType(EvaluationPlanRoundType.HR);
        var updated = new JobEvaluationPlanRound();
        updated.setPublicId("old");
        updated.setName("New");
        updated.setOrder(0);
        updated.setRoundType(EvaluationPlanRoundType.HR);
        job.setEvaluationPlanRounds(List.of(old));
        request.setEvaluationPlanRounds(List.of(EvaluationPlanRoundReqRes.from(updated)));
        request.setRecruitmentPipelineRounds(requestStages(true, "Screening"));
        when(evaluationImpact.preview(any(), any())).thenReturn(new CandidateEvaluationPlanImpact.Preview("fresh", 1, 1));

        assertThrows(CustomException.class, () -> service.update(request));
        request.setEvaluationImpactToken("old");
        request.setConfirmIgnoredEvaluationChanges(true);
        assertThrows(CustomException.class, () -> service.update(request));
        request.setEvaluationImpactToken("fresh");
        request.setConfirmIgnoredEvaluationChanges(false);
        assertThrows(CustomException.class, () -> service.update(request));
        verify(evaluationRounds, never()).archiveByJobPositionId(anyLong());
        verify(evaluationImpact, never()).apply(any(), any(), any());

        request.setConfirmIgnoredEvaluationChanges(true);
        service.update(request);
        verify(evaluationRounds).archiveByJobPositionId(5L);
        verify(evaluationImpact).apply(eq(job), eq(List.of(old)), argThat(updatedRounds ->
                updatedRounds.size() == 1 && "lineage".equals(updatedRounds.get(0).getLineageId())));
    }

    @Test
    void preventsChangingTheTypeOfAnOccupiedEvaluationStage() {
        var extra = stage(90L, "extra", RecruitmentPipelineRoundType.CUSTOM, 4, "Extra");
        existing.add(4, extra);
        for (int i = 0; i < existing.size(); i++) existing.get(i).setOrder(i);
        job.setRecruitmentPipelineRounds(new ArrayList<>(existing));
        var incoming = requestStages(true, "Screening");
        var evaluating = existing.stream().filter(stage -> stage.getRoundType() == RecruitmentPipelineRoundType.EVALUATING)
                .findFirst().orElseThrow();
        incoming.stream().filter(stage -> stage.getId().equals(evaluating.getPublicId()))
                .findFirst().orElseThrow().setRoundType(RecruitmentPipelineRoundType.CUSTOM);
        incoming.stream().filter(stage -> stage.getId().equals("extra"))
                .findFirst().orElseThrow().setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        request.setRecruitmentPipelineRounds(incoming);
        when(applicationService.existsByRecruitmentPipelineStageAndClosedFalse(evaluating.getId())).thenReturn(true);
        var error = assertThrows(CustomException.class, () -> service.update(request));
        assertEquals(CustomException.getFullMessageKey(CustomExceptionType.JOB_PIPELINE_OCCUPIED_STAGE), error.getMessageKey());
        verify(stages, never()).saveAllAndFlush(any());
    }

    @Test
    void readsSourceIdentityFromJobRequest() throws Exception {
        var mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        var req = mapper.readValue("{\"evaluation_plan_rounds\":[{\"id\":\"round-old\"}]}", JobPositionReq.class);
        assertEquals("round-old", req.getEvaluationPlanRounds().get(0).getId());
        assertThrows(com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException.class,
                () -> mapper.readValue("{\"evaluation_plan_rounds\":[{\"lineage_id\":\"untrusted\"}]}", JobPositionReq.class));
    }
}
