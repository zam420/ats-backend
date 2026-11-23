package com.deepconnect.ats.candidatepipeline.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.candidate.dto.CandidateClosureRes;
import com.deepconnect.ats.candidate.dto.CandidateResBase;
import com.deepconnect.ats.candidatepipeline.dto.CandidatePipelineRes;
import com.deepconnect.ats.candidatepipeline.dto.CandidatePipelineStageRes;
import com.deepconnect.ats.candidatepipeline.dto.MoveCandidateReq;
import com.deepconnect.ats.candidatepipeline.dto.RejectCandidateReq;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.dto.JobPositionCandidatePipelineRes;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.jobapplication.model.JobApplication;
import com.deepconnect.ats.jobapplication.enums.JobApplicationCloseType;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CandidatePipelineService {
    private final JobPositionRepository positions;
    private final JobRecruitmentPipelineRoundRepository stages;
    private final JobApplicationRepository applications;
    private final JobPositionAccessService access;

    @Transactional(readOnly = true)
    public List<JobPositionCandidatePipelineRes> jobs() {
        var user = AuthService.getLoggedInUser();
        List<JobPosition> jobs = access.isUserInJobPositionAdminRole(user)
                ? positions.findAllForTenantAdminAsList(user.getTenantId())
                : positions.findAllForTenantNonAdminAsList(user.getTenantId(), user.getUserId(), user.getPublicId());
        return JobPositionCandidatePipelineRes.from(jobs);
    }

    @Transactional(readOnly = true)
    public CandidatePipelineRes board(String jobId) {
        JobPosition job = positions.findByPublicIdAndTenantIdAndDeactivatedFalse(jobId, AuthService.getLoggedInTenantId())
                .orElseThrow(() -> new InternalServerException("Job position not found"));
        access.assertReadAccess(AuthService.getLoggedInUser(), job);
        List<JobRecruitmentPipelineRound> rounds = stages.findByJobPositionIdOrderByOrderAsc(job.getId());
        List<JobApplication> candidates = applications.findByJobPositionIdOrderByCreatedAtDesc(job.getId());
        Map<Long, List<CandidateResBase>> candidatesByRecruitmentPipelineStage = new HashMap<>();
        List<CandidateClosureRes> rejected = new ArrayList<>();

        for (JobRecruitmentPipelineRound round : rounds) 
                candidatesByRecruitmentPipelineStage.put(round.getId(), new ArrayList<>());

        int closedApplicationCount = 0;
        for (JobApplication application : candidates) {
            CandidateResBase candidate = CandidateResBase.from(application);
            if (application.isClosed()) {
                closedApplicationCount++;
                if (application.getApplicationCloseType() != JobApplicationCloseType.REJECTED) {
                    throw new InternalServerException("Unsupported or missing application close type");
                }
                if (application.getConsiderForOtherOpenings() == null) {
                    throw new InternalServerException("Rejected application is missing its consideration preference");
                }
                rejected.add(CandidateClosureRes.from(application));
                continue;
            }

            List<CandidateResBase> stageCandidates = candidatesByRecruitmentPipelineStage.get(application.getRecruitmentPipelineStage());
            if (stageCandidates == null) 
                throw new InternalServerException("Application references a missing pipeline stage");

            stageCandidates.add(candidate);
        }

        List<CandidatePipelineStageRes> stageRes = rounds.stream().map(round -> CandidatePipelineStageRes.from(round, candidatesByRecruitmentPipelineStage.get(round.getId()))).toList();
        return CandidatePipelineRes.from(job.getPublicId(), job.getTitle(), candidates.size(), candidates.size() - closedApplicationCount, stageRes, rejected);
    }

    @Transactional
    public void move(String jobId, String candidateId, MoveCandidateReq request) {
        JobPosition job = positions.findForUpdate(jobId, AuthService.getLoggedInTenantId())
                .orElseThrow(() -> new InternalServerException("Job position not found"));
        access.assertReadAccess(AuthService.getLoggedInUser(), job);
        List<JobRecruitmentPipelineRound> rounds = stages.findByJobPositionIdOrderByOrderAsc(job.getId());
        JobApplication application = applications.findByPublicIdAndJobPositionId(candidateId, job.getId())
                .orElseThrow(() -> new CustomException(CustomExceptionType.JOB_PIPELINE_STALE));
        if (application.isClosed()) throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
        for (int i = 0; i + 1 < rounds.size(); i++) {
            JobRecruitmentPipelineRound from = rounds.get(i);
            JobRecruitmentPipelineRound to = rounds.get(i + 1);
            if (from.getPublicId().equals(request.fromStageId()) && to.getPublicId().equals(request.toStageId())
                    && from.getId().equals(application.getRecruitmentPipelineStage())) {
                application.setRecruitmentPipelineStage(to.getId());
                applications.save(application);
                return;
            }
        }
        throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
    }

    @Transactional
    public void reject(String jobId, String candidateId, RejectCandidateReq request) {
        JobPosition job = positions.findForUpdate(jobId, AuthService.getLoggedInTenantId())
                .orElseThrow(() -> new InternalServerException("Job position not found"));
        access.assertReadAccess(AuthService.getLoggedInUser(), job);
        JobApplication application = applications.findByPublicIdAndJobPositionId(candidateId, job.getId())
                .orElseThrow(() -> new CustomException(CustomExceptionType.JOB_PIPELINE_STALE));
        if (application.isClosed()) throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
        JobRecruitmentPipelineRound stage = stages.findByJobPositionIdOrderByOrderAsc(job.getId()).stream()
                .filter(round -> round.getPublicId().equals(request.stageId())
                        && round.getId().equals(application.getRecruitmentPipelineStage()))
                .findFirst().orElseThrow(() -> new CustomException(CustomExceptionType.JOB_PIPELINE_STALE));
        String reason = request.reason() == null ? null : request.reason().trim();
        application.setClosed(true);
        application.setApplicationCloseType(JobApplicationCloseType.REJECTED);
        application.setClosedReason(reason == null || reason.isEmpty() ? null : reason);
        application.setConsiderForOtherOpenings(request.considerForOtherOpenings());
        application.setClosedAt(LocalDateTime.now());
        application.setClosedAtStageName(stage.getName());
        application.setClosedAtStageType(stage.getRoundType().name());
        applications.save(application);
    }
}
