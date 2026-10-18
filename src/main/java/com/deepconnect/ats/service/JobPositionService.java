package com.deepconnect.ats.service;

import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.model.JobPosition;
import com.deepconnect.ats.repository.JobPositionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPositionService {

    private final JobPositionRepository jobPositionRepository;

    public List<JobPosition> listAll() {
        return jobPositionRepository.findAll();
    }

    public List<JobPosition> listByProjectId(Long projectId) {
        return jobPositionRepository.findByProjectId(projectId);
    }

    public List<JobPosition> listActive() {
        return jobPositionRepository.findByStatus(0);
    }

    public AtsPageRsp<JobPosition> listJobPosition(AtsPageQuery query) {
        Page<JobPosition> page = jobPositionRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    public Optional<JobPosition> getById(Long id) {
        return jobPositionRepository.findById(id);
    }

    @Transactional
    public Long createJobPosition(JobPosition position) {
        Long currentUserId = AuthService.getLoggedInUserId();
        if (currentUserId == null) currentUserId = 1L;

        position.setCreatedById(currentUserId);
        if (position.getStatus() == null) position.setStatus(0);
        return jobPositionRepository.save(position).getId();
    }

    @Transactional
    public Long updateJobPosition(JobPosition position) {
        JobPosition existing = jobPositionRepository.findById(position.getId())
                .orElseThrow(() -> new InternalServerException("Position not found: " + position.getId()));

        if (position.getTitle() != null) existing.setTitle(position.getTitle());
        if (position.getProjectId() != null) existing.setProjectId(position.getProjectId());
        if (position.getRecruitmentType() != null) existing.setRecruitmentType(position.getRecruitmentType());
        if (position.getEmploymentType() != null) existing.setEmploymentType(position.getEmploymentType());
        if (position.getJobType() != null) existing.setJobType(position.getJobType());
        if (position.getWorkLocation() != null) existing.setWorkLocation(position.getWorkLocation());
        if (position.getJobDesc() != null) existing.setJobDesc(position.getJobDesc());
        if (position.getJobReq() != null) existing.setJobReq(position.getJobReq());
        if (position.getHrRecruiterId() != null) existing.setHrRecruiterId(position.getHrRecruiterId());
        if (position.getResumeInbox() != null) existing.setResumeInbox(position.getResumeInbox());
        if (position.getPublishChannel() != null) existing.setPublishChannel(position.getPublishChannel());
        if (position.getStatus() != null) existing.setStatus(position.getStatus());
        if (position.getExtendedQuestions() != null) existing.setExtendedQuestions(position.getExtendedQuestions());
        if (position.getPublicInterviewFeedback() != null) existing.setPublicInterviewFeedback(position.getPublicInterviewFeedback());

        return jobPositionRepository.save(existing).getId();
    }

    @Transactional
    public void publishJob(Long positionId, Integer status) {
        JobPosition position = jobPositionRepository.findById(positionId)
                .orElseThrow(() -> new InternalServerException("Position not found: " + positionId));
        position.setStatus(status);
        if (status == 0) { // active / published
            position.setPublishedAt(LocalDateTime.now());
        }
        jobPositionRepository.save(position);
    }
}
