package com.deepconnect.ats.candidatepipeline.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.candidatepipeline.dto.CandidateEvaluationDto;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationDecision;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationResult;
import com.deepconnect.ats.candidatepipeline.enums.CandidateEvaluationStatus;
import com.deepconnect.ats.candidatepipeline.model.CandidateEvaluationRound;
import com.deepconnect.ats.candidatepipeline.repo.CandidateEvaluationRoundRepository;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.rbac.enums.PermissionType;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.user.repo.UserRepository;
import com.deepconnect.ats.user.dto.UserSummary;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidateEvaluationService {
    private final JobPositionRepository positions;
    private final JobRecruitmentPipelineRoundRepository stages;
    private final JobApplicationRepository applications;
    private final CandidateEvaluationRoundRepository rounds;
    private final JobPositionAccessService access;
    private final UserRepository users;

    @Transactional(readOnly = true)
    public CandidateEvaluationDto.Response get(String jobId, String candidateId) {
        LoggedInUserDetails user = AuthService.getLoggedInUser();
        JobPosition job = positions.findByPublicIdAndTenantIdAndDeactivatedFalse(jobId, user.getTenantId())
                .orElseThrow(() -> new InternalServerException("Job position not found"));
        access.assertReadAccess(user, job);
        JobApplication application = applications.findByPublicIdAndJobPositionId(candidateId, job.getId())
                .orElseThrow(() -> new CustomException(CustomExceptionType.JOB_PIPELINE_STALE));
        List<CandidateEvaluationRound> candidateRounds = rounds.findByJobApplicationIdOrderByOrderAsc(application.getId());
        boolean canManage = !application.isClosed() && application.isEvaluationsInitialized() && isEvaluating(job, application);
        List<UserSummary> interviewers = users.findByTenantIdAndDeactivatedFalseAndLockedFalse(job.getTenantId())
                .stream().map(UserSummary::from).toList();
        return new CandidateEvaluationDto.Response(application.isEvaluationsInitialized(), application.getEvaluationDecision(),
                canManage, canDecide(user, job), candidateRounds.stream().map(CandidateEvaluationDto.Round::from).toList(),
                interviewers);
    }

    /** Caller must check access and hold the job's write lock. */
    @Transactional(propagation = Propagation.MANDATORY)
    public void initializeLocked(JobApplication application, List<JobEvaluationPlanRound> plan) {
        if (application.getId() == null) throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
        List<CandidateEvaluationRound> existing = rounds.findByJobApplicationIdOrderByOrderAsc(application.getId());
        List<CandidateEvaluationRound> snapshots = CandidateEvaluationWorkflow.initialize(application, plan, existing);
        rounds.saveAll(snapshots);
        applications.save(application);
    }

    @Transactional
    public void recordResult(String jobId, String candidateId, String roundId, CandidateEvaluationResult result) {
        Context context = lockContext(jobId, candidateId);
        rounds.save(CandidateEvaluationWorkflow.recordResult(context.application(), context.rounds(), roundId, result));
    }

    @Transactional
    public void changeStatus(String jobId, String candidateId, String roundId, CandidateEvaluationStatus status) {
        Context context = lockContext(jobId, candidateId);
        rounds.save(CandidateEvaluationWorkflow.changeStatus(context.application(), context.rounds(), roundId, status));
    }

    @Transactional
    public void schedule(String jobId, String candidateId, String roundId, Instant scheduledAt, List<String> interviewers) {
        Context context = lockContext(jobId, candidateId);
        validateInterviewers(interviewers, context.job().getTenantId());
        CandidateEvaluationRound round = CandidateEvaluationWorkflow.schedule(context.application(), context.rounds(), roundId, scheduledAt);
        CandidateEvaluationWorkflow.changeInterviewers(context.application(), context.rounds(), roundId, interviewers);
        rounds.save(round);
    }

    @Transactional
    public void addRound(String jobId, String candidateId, String name, String description,
            EvaluationPlanRoundType type, List<String> interviewers) {
        Context context = lockContext(jobId, candidateId);
        validateInterviewers(interviewers, context.job().getTenantId());
        rounds.save(CandidateEvaluationWorkflow.addRound(context.application(), context.rounds(), name, description,
                type, interviewers));
    }

    @Transactional
    public void changeInterviewers(String jobId, String candidateId, String roundId, List<String> interviewers) {
        Context context = lockContext(jobId, candidateId);
        validateInterviewers(interviewers, context.job().getTenantId());
        rounds.save(CandidateEvaluationWorkflow.changeInterviewers(context.application(), context.rounds(), roundId, interviewers));
    }

    @Transactional
    public void decide(String jobId, String candidateId, CandidateEvaluationDecision decision) {
        Context context = lockContext(jobId, candidateId);
        LoggedInUserDetails user = AuthService.getLoggedInUser();
        if (!canDecide(user, context.job())) {
            throw new InternalServerException("Not sufficient access to decide candidate evaluations");
        }
        CandidateEvaluationWorkflow.decide(context.application(), context.rounds(), decision);
        applications.save(context.application());
    }

    private boolean canDecide(LoggedInUserDetails user, JobPosition job) {
        return (user.getUserId() != null && Objects.equals(user.getUserId(), job.getOwnerId()))
                || user.hasPermission(PermissionType.JOB_POSITION_ADMIN)
                || (job.getHiringManagers() != null && job.getHiringManagers().contains(user.getPublicId()));
    }

    private Context lockContext(String jobId, String candidateId) {
        LoggedInUserDetails user = AuthService.getLoggedInUser();
        JobPosition job = positions.findForUpdate(jobId, user.getTenantId())
                .orElseThrow(() -> new InternalServerException("Job position not found"));
        access.assertReadAccess(user, job);
        JobApplication application = applications.findByPublicIdAndJobPositionId(candidateId, job.getId())
                .orElseThrow(() -> new CustomException(CustomExceptionType.JOB_PIPELINE_STALE));
        if (application.isClosed() || !isEvaluating(job, application) || !application.isEvaluationsInitialized()) {
            throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
        }
        return new Context(job, application, rounds.findByJobApplicationIdOrderByOrderAsc(application.getId()));
    }

    private boolean isEvaluating(JobPosition job, JobApplication application) {
        return stages.findByJobPositionIdOrderByOrderAsc(job.getId()).stream()
                .anyMatch(stage -> Objects.equals(stage.getId(), application.getRecruitmentPipelineStage())
                        && stage.getRoundType() == RecruitmentPipelineRoundType.EVALUATING);
    }

    private void validateInterviewers(List<String> interviewers, Long tenantId) {
        if (interviewers == null || interviewers.stream().anyMatch(id -> id == null || id.isBlank())) {
            throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
        }
        Set<String> ids = new HashSet<>(interviewers);
        if (ids.size() != interviewers.size()) throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
        if (ids.isEmpty()) return;
        var matches = users.findByPublicIdInAndDeactivatedFalseAndLockedFalse(ids);
        if (matches.size() != ids.size() || matches.stream().anyMatch(user -> !Objects.equals(user.getTenantId(), tenantId))) {
            throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
        }
    }

    private record Context(JobPosition job, JobApplication application, List<CandidateEvaluationRound> rounds) {}
}
