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

    private String save(JobPosition position, JobPositionReq req){
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        position.setTitle(req.getTitle());
        position.setDescription(req.getDescription());
        position.setMandatoryRequirements(req.getMandatoryRequirements());
        position.setPreferredRequirements(req.getPreferredRequirements());
        position.setEmploymentTypeId(req.getEmploymentType());
        position.setWorkArrangementTypeId(req.getWorkArrangementType());
        position.setWorkLocation(req.getWorkLocation());
        position.setRecruiters(req.getRecruiters());
        position.setRecruitmentPipelinePublicId(req.getRecruitmentPipeline());
        position.setEvaluationPlanPublicId(req.getEvaluationPlan());
        EvaluationPlan evaluationPlan = evaluationPlanService.findByPublicId(position.getEvaluationPlanPublicId()).orElseThrow(() -> new InternalServerException(String.format("Evaluation plan not found: User: %s, Evaluation Plan: %s", loggedInUser.getPublicId(), position.getEvaluationPlanPublicId())));
        position.setEvaluationPlanId(evaluationPlan.getId());
        RecruitmentPipeline recruitmentPipeline = recruitmentPipelineService.findByPublicId(position.getRecruitmentPipelinePublicId()).orElseThrow(() -> new InternalServerException(String.format("Recruitment pipeline not found: User: %s, Recruitment Pipeline: %s", loggedInUser.getPublicId(), position.getRecruitmentPipelinePublicId())));
        position.setRecruitmentPipelineId(recruitmentPipeline.getId());
        return jobPositionRepository.save(position).getPublicId();
    }

    @Transactional
    public String create(JobPositionReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        //this.assertUniqueWithinTenantForCreation(loggedInUser.getTenantId(), req.getName());
        JobPosition jobPosition = new JobPosition();
        jobPosition.setCreatedByUserId(loggedInUser.getUserId());
        jobPosition.setCreatedByUserPublicId(loggedInUser.getPublicId());
        jobPosition.setTenantId(loggedInUser.getTenantId());
        if(jobPosition.getOwnerId() == null) {
            jobPosition.setOwnerId(loggedInUser.getUserId());
            jobPosition.setOwnerPublicId(loggedInUser.getPublicId());
        }
        // else {
        //     setAnotherUserAsOwner(plan, evaluationPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        // }

        jobPosition.setStatus(JobStatus.DRAFT);
        return this.save(jobPosition, req);
    }

    @Transactional 
    public String update(JobPositionReq req) {
        if (req.getId() == null) 
            throw new InternalServerException(String.format("Invalid Job Position Id: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), req.getId()));

        JobPosition jobPosition = jobPositionRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException(String.format("Job Position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), req.getId())));

        // LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        //this.assertUniqueWithinTenantForUpdate(loggedInUser.getTenantId(), req.getName(), plan.getId());
        //evaluationPlanAccessService.assertWriteAccess(loggedInUser, plan);
        // if(!loggedInUser.getPublicId().equals(req.getOwnerId())){//if the requested owner is not the logged in user, additional checks are needed
        //     setAnotherUserAsOwner(plan, evaluationPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        // }

        //boolean roundsChanged = !roundsEqual(plan.getRounds(), req.getRounds());
        //if (roundsChanged) {
        //    evaluationPlanRoundRepository.deleteByEvaluationPlanId(plan.getId());
        //}

        return this.save(jobPosition, req);
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
