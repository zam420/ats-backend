package com.deepconnect.ats.job.service;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.job.dto.JobFieldsData;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobEmploymentTypeRepository;
import com.deepconnect.ats.job.repo.JobWorkArrangementTypeRepository;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.user.service.UserServiceV2;
import com.deepconnect.ats.common.enums.RoleType;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.job.dto.JobPositionRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPositionService {

    private final EvaluationPlanService evaluationPlanService;
    private final RecruitmentPipelineService recruitmentPipelineService;
    private final UserServiceV2 userService;

    private final JobPositionRepository jobPositionRepository;
    private final JobEmploymentTypeRepository jobEmploymentTypeRepository;
    private final JobWorkArrangementTypeRepository jobWorkArrangementTypeRepository;

    @Transactional
    public String create(JobPositionReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        //this.assertUniqueWithinTenantForCreation(loggedInUser.getTenantId(), req.getName());
        JobPosition position = req.toEntity();
        EvaluationPlan evaluationPlan = evaluationPlanService.findByPublicId(position.getEvaluationPlanPublicId()).orElseThrow(() -> new InternalServerException(String.format("Evaluation plan not found: User: %s, Evaluation Plan: %s", loggedInUser.getPublicId(), position.getEvaluationPlanPublicId())));
        position.setEvaluationPlanId(evaluationPlan.getId());
        RecruitmentPipeline recruitmentPipeline = recruitmentPipelineService.findByPublicId(position.getRecruitmentPipelinePublicId()).orElseThrow(() -> new InternalServerException(String.format("Recruitment pipeline not found: User: %s, Recruitment Pipeline: %s", loggedInUser.getPublicId(), position.getRecruitmentPipelinePublicId())));
        position.setRecruitmentPipelineId(recruitmentPipeline.getId());
        position.setCreatedByUserId(loggedInUser.getUserId());
        position.setCreatedByUserPublicId(loggedInUser.getPublicId());
        position.setTenantId(loggedInUser.getTenantId());
        if(position.getOwnerId() == null) {
            position.setOwnerId(loggedInUser.getUserId());
            position.setOwnerPublicId(loggedInUser.getPublicId());
        }
        else {
            // setAnotherUserAsOwner(plan, evaluationPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        }

        position.setStatus(JobStatus.DRAFT);
        return jobPositionRepository.save(position).getPublicId();
    }

    @Transactional
    public void publish(String positionId, Integer status) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        JobPosition position = jobPositionRepository.findByPublicId(positionId)
                .orElseThrow(() -> new InternalServerException("Position not found: " + positionId));
        position.setStatus(JobStatus.OPEN);
        position.setPublishedByUserId(loggedInUser.getUserId());
        position.setPublishedByUserPublicId(loggedInUser.getPublicId());
        jobPositionRepository.save(position);
    }

    // public List<JobPosition> listAll() {
    //     return jobPositionRepository.findAll();
    // }

    // public List<JobPosition> listByProjectId(Long projectId) {
    //     return jobPositionRepository.findByProjectId(projectId);
    // }

    @Transactional(readOnly = true)
    public JobPositionRes findByPublicIdForTenant(String publicId) {
        JobPosition position = jobPositionRepository.findByPublicIdAndTenantId(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
        return JobPositionRes.from(position);
    }

    public List<JobPositionRes> findAll() {
        List<JobPosition> positions = jobPositionRepository.findByStatus(JobStatus.DRAFT);
        return  JobPositionRes.fromList(positions);
    }

    public AtsPageRsp<JobPosition> listJobPosition(AtsPageQuery query) {
        Page<JobPosition> page = jobPositionRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    public Optional<JobPosition> findById(Long id) {
        return jobPositionRepository.findById(id);
    }

    @Transactional
    public String update(JobPosition position) {
        JobPosition existing = jobPositionRepository.findById(position.getId())
                .orElseThrow(() -> new InternalServerException("Position not found: " + position.getId()));

        if (position.getTitle() != null) existing.setTitle(position.getTitle());
        if (position.getEmploymentType() != null) existing.setEmploymentType(position.getEmploymentType());
        if (position.getWorkLocation() != null) existing.setWorkLocation(position.getWorkLocation());
        if (position.getDescription() != null) existing.setDescription(position.getDescription());
        if (position.getMandatoryRequirements() != null) existing.setMandatoryRequirements(position.getMandatoryRequirements());
        if (position.getPreferredRequirements() != null) existing.setPreferredRequirements(position.getPreferredRequirements());
        existing.setRecruiters(position.getRecruiters());
        if (position.getStatus() != null) existing.setStatus(position.getStatus());
        if (position.getExtendedQuestions() != null) existing.setExtendedQuestions(position.getExtendedQuestions());

        return jobPositionRepository.save(existing).getPublicId();
    }

    public JobFieldsData getJobFieldsData() {
        return JobFieldsData.from(
            jobEmploymentTypeRepository.findAll(),
            jobWorkArrangementTypeRepository.findAll(),
            evaluationPlanService.findAllAsList(),
            recruitmentPipelineService.findAllAsList(),
            userService.findActiveByRolesForTenant(Set.of(RoleType.RECRUITER))
        );
    }

}
