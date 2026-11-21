package com.deepconnect.ats.jobapplication.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.service.JobPositionAccessService;
import com.deepconnect.ats.jobapplication.dto.CandidatePipelineRes;
import com.deepconnect.ats.jobapplication.dto.MoveCandidateReq;
import com.deepconnect.ats.jobapplication.model.JobApplication;
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
    public List<CandidatePipelineRes.Job> jobs() {
        var user = AuthService.getLoggedInUser();
        List<JobPosition> jobs = access.isUserInJobPositionAdminRole(user)
                ? positions.findAllForTenantAdminAsList(user.getTenantId())
                : positions.findAllForTenantNonAdminAsList(user.getTenantId(), user.getUserId(), user.getPublicId());
        return jobs.stream().map(job -> new CandidatePipelineRes.Job(job.getPublicId(), job.getTitle())).toList();
    }

    @Transactional(readOnly = true)
    public CandidatePipelineRes board(String jobId) {
        JobPosition job = positions.findByPublicIdAndTenantIdAndDeactivatedFalse(jobId, AuthService.getLoggedInTenantId())
                .orElseThrow(() -> new InternalServerException("Job position not found"));
        access.assertReadAccess(AuthService.getLoggedInUser(), job);
        List<JobRecruitmentPipelineRound> rounds = stages.findByJobPositionIdOrderByOrderAsc(job.getId());
        List<JobApplication> candidates = applications.findByJobPositionIdOrderByCreatedAtDesc(job.getId());
        Map<Long, List<CandidatePipelineRes.Candidate>> grouped = new HashMap<>();
        for (JobRecruitmentPipelineRound round : rounds) grouped.put(round.getId(), new ArrayList<>());
        for (JobApplication candidate : candidates) {
            List<CandidatePipelineRes.Candidate> group = grouped.get(candidate.getRecruitmentPipelineStage());
            if (group == null) throw new InternalServerException("Application references a missing pipeline stage");
            group.add(new CandidatePipelineRes.Candidate(candidate.getPublicId(), candidate.getFirstName(),
                    candidate.getLastName(), candidate.getPreferredName(), candidate.getEmail(), candidate.getCurrentLocation(),
                    candidate.getCreatedAt()));
        }
        return new CandidatePipelineRes(job.getPublicId(), job.getTitle(), candidates.size(),
                rounds.stream().map(round -> new CandidatePipelineRes.Stage(round.getPublicId(), round.getName(),
                        round.getRoundType().name(), round.getOrder(), grouped.get(round.getId()))).toList());
    }

    @Transactional
    public void move(String jobId, String candidateId, MoveCandidateReq request) {
        JobPosition job = positions.findForUpdate(jobId, AuthService.getLoggedInTenantId())
                .orElseThrow(() -> new InternalServerException("Job position not found"));
        access.assertReadAccess(AuthService.getLoggedInUser(), job);
        List<JobRecruitmentPipelineRound> rounds = stages.findByJobPositionIdOrderByOrderAsc(job.getId());
        JobApplication application = applications.findByPublicIdAndJobPositionId(candidateId, job.getId())
                .orElseThrow(() -> new CustomException(CustomExceptionType.JOB_PIPELINE_STALE));
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
}
