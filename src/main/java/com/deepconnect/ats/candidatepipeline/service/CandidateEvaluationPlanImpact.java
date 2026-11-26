package com.deepconnect.ats.candidatepipeline.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.candidatepipeline.repo.CandidateEvaluationRoundRepository;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.service.EnumsResService;
import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CandidateEvaluationPlanImpact {
    private final JobApplicationRepository applications;
    private final JobRecruitmentPipelineRoundRepository stages;
    private final CandidateEvaluationRoundRepository rounds;
    private final EnumsResService enums;
    private final ObjectMapper mapper;

    public record Preview(String token, int candidates, int ignoredCandidates) {}
    private record PlanState(Long id, String publicId, String lineage, Integer order, String name,
            String description, String type, List<String> interviewers) {}
    private record RoundState(Long id, String publicId, Long planId, String lineage, Integer order, String name,
            String description, String type, List<String> interviewers, String status, String result, String scheduledAt) {}
    private record CandidateState(Long id, Long stage, String decision, List<RoundState> rounds) {}
    private record Snapshot(Long jobId, Long tenantId, List<PlanState> previous, List<PlanState> proposed,
            List<CandidateState> candidates) {}

    /** Caller holds the job write lock and has checked write access. */
    @Transactional(propagation = Propagation.MANDATORY)
    public Preview preview(JobPosition job, List<JobEvaluationPlanRound> incoming) {
        List<JobEvaluationPlanRound> previous = job.getEvaluationPlanRounds() == null
                ? List.of() : job.getEvaluationPlanRounds();
        List<JobEvaluationPlanRound> projected = project(job, previous, incoming);
        List<JobApplication> candidates = activeCandidates(job);
        int ignored = 0;
        List<CandidateState> candidateStates = new ArrayList<>();
        for (JobApplication candidate : candidates) {
            List<CandidateEvaluationRound> existing = rounds.findByJobApplicationIdOrderByOrderAsc(candidate.getId());
            List<CandidateEvaluationRound> copies = existing.stream().map(CandidateEvaluationPlanImpact::copy).toList();
            if (CandidateEvaluationPlanSync.synchronize(candidate, copies, previous, projected).hasIgnoredChanges()) ignored++;
            candidateStates.add(new CandidateState(candidate.getId(), candidate.getRecruitmentPipelineStage(),
                    candidate.getEvaluationDecision() == null ? null : candidate.getEvaluationDecision().name(),
                    existing.stream().map(CandidateEvaluationPlanImpact::roundState).toList()));
        }
        Snapshot state = new Snapshot(job.getId(), job.getTenantId(),
                previous.stream().map(CandidateEvaluationPlanImpact::planState).toList(),
                projected.stream().map(CandidateEvaluationPlanImpact::planState).toList(), candidateStates);
        try {
            return new Preview(HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(mapper.writeValueAsString(state).getBytes(StandardCharsets.UTF_8))), candidates.size(), ignored);
        } catch (JsonProcessingException | NoSuchAlgorithmException e) {
            log.error("Failed to calculate evaluation plan impact", e);
            throw new InternalServerException("Failed to calculate evaluation plan impact");
        }
    }

    /** Caller holds the job write lock and has confirmed the matching preview. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void apply(JobPosition job, List<JobEvaluationPlanRound> previous, List<JobEvaluationPlanRound> updated) {
        for (JobApplication candidate : activeCandidates(job)) {
            List<CandidateEvaluationRound> existing = rounds.findByJobApplicationIdOrderByOrderAsc(candidate.getId());
            var result = CandidateEvaluationPlanSync.synchronize(candidate, existing, previous, updated);
            if (!result.removed().isEmpty()) {
                rounds.deleteAll(result.removed());
                rounds.flush();
            }
            rounds.saveAll(result.rounds());
        }
    }

    private List<JobApplication> activeCandidates(JobPosition job) {
        Set<Long> evaluationStages = stages.findByJobPositionIdOrderByOrderAsc(job.getId()).stream()
                .filter(stage -> stage.getRoundType() == RecruitmentPipelineRoundType.EVALUATING)
                .map(stage -> stage.getId()).collect(java.util.stream.Collectors.toSet());
        return applications.findByJobPositionIdOrderByCreatedAtDesc(job.getId()).stream()
                .filter(application -> !application.isClosed() && application.isEvaluationsInitialized()
                        && evaluationStages.contains(application.getRecruitmentPipelineStage()))
                .sorted(Comparator.comparing(JobApplication::getId)).toList();
    }

    private List<JobEvaluationPlanRound> project(JobPosition job, List<JobEvaluationPlanRound> previous,
            List<JobEvaluationPlanRound> incoming) {
        if (incoming == null || incoming.isEmpty()) throw new CustomException(CustomExceptionType.JOB_PIPELINE_INVALID);
        Map<String, JobEvaluationPlanRound> byPublicId = new HashMap<>();
        previous.forEach(round -> byPublicId.put(round.getPublicId(), round));
        Set<String> used = new HashSet<>();
        Set<Integer> orders = new HashSet<>();
        List<JobEvaluationPlanRound> projected = new ArrayList<>();
        for (int i = 0; i < incoming.size(); i++) {
            JobEvaluationPlanRound source = incoming.get(i);
            if (source == null || source.getRoundType() == null || source.getOrder() == null
                    || source.getOrder() < 0 || !orders.add(source.getOrder())) {
                throw new CustomException(CustomExceptionType.JOB_PIPELINE_INVALID);
            }
            String lineage = "new:" + i;
            if (source.getSourcePublicId() != null) {
                JobEvaluationPlanRound old = byPublicId.get(source.getSourcePublicId());
                if (old == null || old.getLineageId() == null || !used.add(source.getSourcePublicId())) {
                    throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
                }
                lineage = old.getLineageId();
            }
            JobEvaluationPlanRound copy = new JobEvaluationPlanRound();
            copy.setId(-(long) i - 1);
            copy.setJobPositionId(job.getId());
            copy.setLineageId(lineage);
            copy.setOrder(source.getOrder());
            copy.setName(StringUtils.hasText(source.getName()) ? source.getName() : enums.from(source.getRoundType()).getName());
            copy.setDescription(source.getDescription());
            copy.setRoundType(source.getRoundType());
            copy.setInterviewers(source.getInterviewers() == null ? null : new ArrayList<>(source.getInterviewers()));
            projected.add(copy);
        }
        return projected;
    }

    private static CandidateEvaluationRound copy(CandidateEvaluationRound source) {
        CandidateEvaluationRound copy = new CandidateEvaluationRound();
        copy.setId(source.getId());
        copy.setPublicId(source.getPublicId());
        copy.setJobApplicationId(source.getJobApplicationId());
        copy.setPlanRoundId(source.getPlanRoundId());
        copy.setPlanLineageId(source.getPlanLineageId());
        copy.setOrder(source.getOrder());
        copy.setName(source.getName());
        copy.setDescription(source.getDescription());
        copy.setRoundType(source.getRoundType());
        copy.setInterviewers(source.getInterviewers() == null ? null : new ArrayList<>(source.getInterviewers()));
        copy.setStatus(source.getStatus());
        copy.setResult(source.getResult());
        copy.setScheduledAt(source.getScheduledAt());
        return copy;
    }

    private static PlanState planState(JobEvaluationPlanRound round) {
        return new PlanState(round.getId(), round.getPublicId(), round.getLineageId(), round.getOrder(), round.getName(),
                round.getDescription(), round.getRoundType() == null ? null : round.getRoundType().name(), round.getInterviewers());
    }

    private static RoundState roundState(CandidateEvaluationRound round) {
        return new RoundState(round.getId(), round.getPublicId(), round.getPlanRoundId(), round.getPlanLineageId(),
                round.getOrder(), round.getName(), round.getDescription(),
                round.getRoundType() == null ? null : round.getRoundType().name(), round.getInterviewers(),
                round.getStatus() == null ? null : round.getStatus().name(),
                round.getResult() == null ? null : round.getResult().name(),
                Objects.toString(round.getScheduledAt(), null));
    }
}
