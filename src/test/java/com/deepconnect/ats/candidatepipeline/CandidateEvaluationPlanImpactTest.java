package com.deepconnect.ats.candidatepipeline;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.repo.CandidateEvaluationRoundRepository;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationPlanImpact;
import com.deepconnect.ats.candidatepipeline.service.CandidateEvaluationWorkflow;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.service.EnumsResService;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.fasterxml.jackson.databind.ObjectMapper;

class CandidateEvaluationPlanImpactTest {
    private JobEvaluationPlanRound plan(long id, String publicId, String lineage, int order) {
        var round = new JobEvaluationPlanRound();
        round.setId(id);
        round.setJobPositionId(5L);
        round.setPublicId(publicId);
        round.setLineageId(lineage);
        round.setOrder(order);
        round.setName(publicId);
        round.setRoundType(EvaluationPlanRoundType.HR);
        return round;
    }

    @Test
    void previewsWithoutMutatingAndTokenChangesWithPlanOrCandidateState() {
        var applications = mock(JobApplicationRepository.class);
        var stages = mock(JobRecruitmentPipelineRoundRepository.class);
        var rounds = mock(CandidateEvaluationRoundRepository.class);
        var impact = new CandidateEvaluationPlanImpact(applications, stages, rounds, mock(EnumsResService.class),
                new ObjectMapper().findAndRegisterModules());
        var a = plan(1, "a", "lineage-a", 0);
        var b = plan(2, "b", "lineage-b", 1);
        var job = new JobPosition();
        job.setId(5L);
        job.setTenantId(2L);
        job.setEvaluationPlanRounds(List.of(a, b));
        var application = new JobApplication();
        application.setId(7L);
        application.setJobPositionId(5L);
        application.setRecruitmentPipelineStage(10L);
        var candidateRounds = new ArrayList<>(CandidateEvaluationWorkflow.initialize(application, List.of(a, b), List.of()));
        candidateRounds.get(0).setPublicId("round-a");
        candidateRounds.get(1).setPublicId("round-b");
        CandidateEvaluationWorkflow.recordResult(application, candidateRounds, "round-a", CandidateEvaluationResult.PASSED);
        var stage = new JobRecruitmentPipelineRound();
        stage.setId(10L);
        stage.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(stage));
        when(applications.findByJobPositionIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(application));
        when(rounds.findByJobApplicationIdOrderByOrderAsc(7L)).thenReturn(candidateRounds);
        var inserted = plan(99, "new", null, 1);
        var updatedA = plan(98, "a", null, 0);
        updatedA.setSourcePublicId("a");
        var updatedB = plan(97, "b", null, 2);
        updatedB.setSourcePublicId("b");
        var proposed = List.of(updatedA, inserted, updatedB);

        var preview = impact.preview(job, proposed);
        assertEquals(1, preview.ignoredCandidates());
        assertEquals(1, preview.candidates());
        assertEquals(preview.token(), impact.preview(job, proposed).token());
        assertEquals("b", candidateRounds.get(1).getName());
        assertEquals(1, candidateRounds.get(1).getOrder());
        inserted.setName("Changed");
        assertNotEquals(preview.token(), impact.preview(job, proposed).token());
        inserted.setName("new");
        CandidateEvaluationWorkflow.recordResult(application, candidateRounds, "round-b", CandidateEvaluationResult.PASSED);
        assertNotEquals(preview.token(), impact.preview(job, proposed).token());
        updatedA.setSourcePublicId("stale");
        assertThrows(CustomException.class, () -> impact.preview(job, proposed));
    }

    @Test
    void appliesChangesToPendingRoundsAndDeletesRemovedRounds() {
        var applications = mock(JobApplicationRepository.class);
        var stages = mock(JobRecruitmentPipelineRoundRepository.class);
        var rounds = mock(CandidateEvaluationRoundRepository.class);
        var impact = new CandidateEvaluationPlanImpact(applications, stages, rounds, mock(EnumsResService.class),
                new ObjectMapper());
        var a = plan(1, "a", "lineage-a", 0);
        var b = plan(2, "b", "lineage-b", 1);
        var job = new JobPosition();
        job.setId(5L);
        var application = new JobApplication();
        application.setId(7L);
        application.setJobPositionId(5L);
        application.setRecruitmentPipelineStage(10L);
        var candidateRounds = new ArrayList<>(CandidateEvaluationWorkflow.initialize(application, List.of(a, b), List.of()));
        candidateRounds.get(0).setPublicId("round-a");
        candidateRounds.get(1).setPublicId("round-b");
        var stage = new JobRecruitmentPipelineRound();
        stage.setId(10L);
        stage.setRoundType(RecruitmentPipelineRoundType.EVALUATING);
        when(stages.findByJobPositionIdOrderByOrderAsc(5L)).thenReturn(List.of(stage));
        when(applications.findByJobPositionIdOrderByCreatedAtDesc(5L)).thenReturn(List.of(application));
        when(rounds.findByJobApplicationIdOrderByOrderAsc(7L)).thenReturn(candidateRounds);
        var updatedB = plan(12, "new-b", "lineage-b", 0);
        impact.apply(job, List.of(a, b), List.of(updatedB));
        verify(rounds).deleteAll(List.of(candidateRounds.get(0)));
        verify(rounds).flush();
        verify(rounds).saveAll(List.of(candidateRounds.get(1)));
        assertEquals("new-b", candidateRounds.get(1).getName());
        assertEquals(12L, candidateRounds.get(1).getPlanRoundId());
    }
}
