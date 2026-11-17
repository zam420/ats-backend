package com.deepconnect.ats.job.service;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.ValidationException;
import com.deepconnect.ats.common.utils.HashMapUtils;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.job.dto.JobFiltersData;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.security.JobRichTextSanitizer;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobEmploymentTypeRepository;
import com.deepconnect.ats.job.repo.JobExperienceLevelTypeRepository;
import com.deepconnect.ats.job.repo.JobEvaluationPlanRoundRepository;
import com.deepconnect.ats.job.repo.JobWorkArrangementTypeRepository;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanRoundHelper;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineRoundHelper;
import com.deepconnect.ats.user.service.UserServiceV2;
import com.deepconnect.ats.rbac.enums.RoleType;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.enums.JobStatus;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundType;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundType;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.repo.JobScreeningQuestionRepository;
import com.deepconnect.ats.job.dto.JobPositionRes;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionHelper;
import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.common.enums.ValidationFieldType;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobPositionService {

    @Value("${plans.maxRounds}")
    private int maxRounds;

    private final EvaluationPlanService evaluationPlanService;
    private final RecruitmentPipelineService recruitmentPipelineService;
    private final JobPositionAccessService jobPositionAccessService;
    private final ScreeningQuestionsFormService screeningQuestionsFormService;
    private final UserServiceV2 userService;

    private final JobPositionRepository jobPositionRepository;
    private final JobEmploymentTypeRepository jobEmploymentTypeRepository;
    private final JobWorkArrangementTypeRepository jobWorkArrangementTypeRepository;
    private final JobExperienceLevelTypeRepository jobExperienceLevelTypeRepository;
    private final JobEvaluationPlanRoundRepository jobEvaluationPlanRoundRepository;
    private final JobRecruitmentPipelineRoundRepository jobRecruitmentPipelineRoundRepository;
    private final JobScreeningQuestionRepository jobScreeningQuestionRepository;

    private String save(JobPosition position, JobPositionReq req, boolean evaluationPlanRoundsChanged, boolean recruitmentPipelineRoundsChanged, boolean screeningQuestionsChanged){
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        position.setModifiedByUserId(loggedInUser.getUserId());
        position.setModifiedByUserPublicId(loggedInUser.getPublicId());
        
        position.setTitle(req.getTitle());
        position.setDescription(JobRichTextSanitizer.sanitize(req.getDescription()));
        position.setEmploymentTypeId(req.getEmploymentType());
        position.setWorkArrangementTypeId(req.getWorkArrangementType());
        position.setExperienceLevelTypeId(req.getExperienceLevelType());
        position.setWorkLocation(req.getWorkLocation());
        position.setRequisitionId(req.getRequisitionId());
        position.setNumOfOpenings(req.getNumOfOpenings());
        position.setOffice(req.getOffice());
        position.setDepartment(req.getDepartment());
        position.setRecruiters(req.getRecruiters());
        position.setHiringManagers(req.getHiringManagers());
        position.setApplicationQuestionsDisplayMode(req.getApplicationQuestionsDisplayMode());

        position = jobPositionRepository.save(position);
        if (evaluationPlanRoundsChanged){
            if(CollectionUtils.isEmpty(req.getEvaluationPlanRounds())) {
                throw new ValidationException(CustomExceptionType.VALIDATION_MUST_ADD, ValidationFieldType.EVALUATION_PLAN_ROUND);
            }
            else{
                if (req.getEvaluationPlanRounds().size() > maxRounds) {
                    throw new InternalServerException(String.format("Too many rounds. User: %s. Plan: %s. Rounds: %d", loggedInUser.getPublicId(), position.getPublicId(), req.getEvaluationPlanRounds().size()));
                }
                HashMap<Integer, EvaluationPlanRoundType> roundTypesById = HashMapUtils.getMapFromList(evaluationPlanService.findAllRoundTypes(), "Id", Integer.class, EvaluationPlanRoundType.class);
                for (JobEvaluationPlanRound roundTemplate : req.getEvaluationPlanRounds()) {
                    roundTemplate.setJobPosition(position);
                    roundTemplate.setJobPositionId(position.getId());
                    if(!StringUtils.hasText(roundTemplate.getName())) {
                        EvaluationPlanRoundType roundType = roundTypesById.get(roundTemplate.getRoundType());
                        if(roundType != null)
                            roundTemplate.setName(roundType.getName());
                    }
                }
                jobEvaluationPlanRoundRepository.saveAll(req.getEvaluationPlanRounds());
            }
        }

        if (recruitmentPipelineRoundsChanged) {
            if(CollectionUtils.isEmpty(req.getRecruitmentPipelineRounds())) {
                throw new ValidationException(CustomExceptionType.VALIDATION_MUST_ADD, ValidationFieldType.RECRUITMENT_PIPELINE_STAGE);
            }
            else{
                if (req.getRecruitmentPipelineRounds().size() > maxRounds) {
                    throw new InternalServerException(String.format("Too many rounds. User: %s. Position: %s. Rounds: %d", loggedInUser.getPublicId(), position.getPublicId(), req.getRecruitmentPipelineRounds().size()));
                }
                List<RecruitmentPipelineRoundType> allRoundTypes = recruitmentPipelineService.findAllRoundTypes();
                RecruitmentPipelineRoundHelper.assertRoundTypeOrder(allRoundTypes, req.getRecruitmentPipelineRounds(), loggedInUser.getPublicId(), position.getPublicId());
                HashMap<Integer, RecruitmentPipelineRoundType> roundTypesById = HashMapUtils.getMapFromList(allRoundTypes, "Id", Integer.class, RecruitmentPipelineRoundType.class);
                for (JobRecruitmentPipelineRound roundTemplate : req.getRecruitmentPipelineRounds()) {
                    roundTemplate.setJobPosition(position);
                    roundTemplate.setJobPositionId(position.getId());
                    if(!StringUtils.hasText(roundTemplate.getName())) {
                        RecruitmentPipelineRoundType roundType = roundTypesById.get(roundTemplate.getRoundType());
                        if(roundType != null)
                            roundTemplate.setName(roundType.getName());
                    }
                }
                jobRecruitmentPipelineRoundRepository.saveAll(req.getRecruitmentPipelineRounds());
            }
        }

        if (screeningQuestionsChanged && !CollectionUtils.isEmpty(req.getScreeningQuestions())) {
            if (req.getScreeningQuestions().size() > maxRounds) {
                throw new InternalServerException(String.format("Too many questions. User: %s. Position: %s. Questions: %d", loggedInUser.getPublicId(), position.getPublicId(), req.getScreeningQuestions().size()));
            }
            for (JobScreeningQuestion question : req.getScreeningQuestions()) {
                question.setJobPosition(position);
                question.setJobPositionId(position.getId());
            }
            jobScreeningQuestionRepository.saveAll(req.getScreeningQuestions());
        }

        return position.getPublicId();
    }

    @Transactional
    public String create(JobPositionReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        //TODO: think about uniqueness criteria and implement //this.assertUniqueWithinTenantForCreation(loggedInUser.getTenantId(), req.getName());
        JobPosition jobPosition = new JobPosition();
        jobPosition.setCreatedByUserId(loggedInUser.getUserId());
        jobPosition.setCreatedByUserPublicId(loggedInUser.getPublicId());
        jobPosition.setTenantId(loggedInUser.getTenantId());
        //TODO: think about allowing other users as job owner just like evaluation plan
        // if(req.getOwnerId() == null || req.getOwnerId().equals(loggedInUser.getPublicId())) {
            jobPosition.setOwnerId(loggedInUser.getUserId());
            jobPosition.setOwnerPublicId(loggedInUser.getPublicId());
        // }
        // else {
        //     setAnotherUserAsOwner(plan, evaluationPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        // }

        jobPosition.setStatus(JobStatus.DRAFT);
        return this.save(jobPosition, req, true, true, true);
    }

    @Transactional
    public String update(JobPositionReq req) {
        if (req.getId() == null) 
            throw new InternalServerException(String.format("Invalid Job Position Id: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        JobPosition jobPosition = jobPositionRepository.findByPublicIdAndTenantIdAndDeactivatedFalse(req.getId(), loggedInUser.getTenantId())
                .orElseThrow(() -> new InternalServerException(String.format("Job Position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), req.getId())));

        //TODO: think about uniqueness criteria and implement //
        //this.assertUniqueWithinTenantForUpdate(loggedInUser.getTenantId(), req.getName(), plan.getId());
        jobPositionAccessService.assertWriteAccess(loggedInUser, jobPosition);
        // if(!loggedInUser.getPublicId().equals(req.getOwnerId())){//if the requested owner is not the logged in user, additional checks are needed
        //     setAnotherUserAsOwner(plan, evaluationPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        // }

        boolean evaluationPlanRoundsChanged = !EvaluationPlanRoundHelper.roundsEqual(jobPosition.getEvaluationPlanRounds(), req.getEvaluationPlanRounds());
        if (evaluationPlanRoundsChanged) {
           jobEvaluationPlanRoundRepository.deleteByJobPositionId(jobPosition.getId());
        }

        boolean recruitmentPipelineRoundsChanged = !RecruitmentPipelineRoundHelper.roundsEqual(jobPosition.getRecruitmentPipelineRounds(), req.getRecruitmentPipelineRounds());
        if (recruitmentPipelineRoundsChanged) {
           jobRecruitmentPipelineRoundRepository.deleteByJobPositionId(jobPosition.getId());
        }

        boolean screeningQuestionsChanged = !ScreeningQuestionHelper.questionsEqual(jobPosition.getScreeningQuestions(), req.getScreeningQuestions());
        if (screeningQuestionsChanged) {
           jobScreeningQuestionRepository.deleteByJobPositionId(jobPosition.getId());
        }

        return this.save(jobPosition, req, evaluationPlanRoundsChanged, recruitmentPipelineRoundsChanged, screeningQuestionsChanged);
    }

    @Transactional
    public void publish(String publicId) {
        JobPosition jobPosition = jobPositionRepository.findByPublicIdAndTenantIdAndDeactivatedFalse(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
        if (jobPosition.getStatus() != JobStatus.DRAFT) {
            throw new CustomException(CustomExceptionType.JOB_INVALID_STATUS_TRANSITION);
        }
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        jobPosition.setStatus(JobStatus.OPEN);
        jobPosition.setPublishedByUserId(loggedInUser.getUserId());
        jobPosition.setPublishedByUserPublicId(loggedInUser.getPublicId());
        jobPosition.setPublishedAt(LocalDateTime.now());
        this.saveForStatusUpdate(jobPosition);
    }

    @Transactional
    public void close(String publicId) {
        JobPosition jobPosition = jobPositionRepository.findByPublicIdAndTenantIdAndDeactivatedFalse(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
        if (jobPosition.getStatus() != JobStatus.OPEN) {
            throw new CustomException(CustomExceptionType.JOB_INVALID_STATUS_TRANSITION);
        }
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        jobPosition.setStatus(JobStatus.CLOSED);
        jobPosition.setClosedByUserId(loggedInUser.getUserId());
        jobPosition.setClosedByUserPublicId(loggedInUser.getPublicId());
        this.saveForStatusUpdate(jobPosition);
    }

    @Transactional
    public void deactivate(String publicId) {
        JobPosition jobPosition = jobPositionRepository.findByPublicIdAndTenantIdAndDeactivatedFalse(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
        jobPosition.setDeactivated(true);
        this.saveForStatusUpdate(jobPosition);
    }

    private void saveForStatusUpdate(JobPosition jobPosition) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        jobPositionAccessService.assertWriteAccess(loggedInUser, jobPosition);
        jobPosition.setModifiedByUserId(loggedInUser.getUserId());
        jobPosition.setModifiedByUserPublicId(loggedInUser.getPublicId());
        jobPositionRepository.save(jobPosition);
    }

    // public List<JobPosition> listAll() {
    //     return jobPositionRepository.findAll();
    // }

    // public List<JobPosition> listByProjectId(Long projectId) {
    //     return jobPositionRepository.findByProjectId(projectId);
    // }

    @Transactional(readOnly = true)
    public JobPositionRes findByPublicIdForTenant(String publicId) {
        JobPosition position = jobPositionRepository.findByPublicIdAndTenantIdAndDeactivatedFalse(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
        jobPositionAccessService.assertReadAccess(AuthService.getLoggedInUser(), position);
        return JobPositionRes.from(position);
    }

     @Transactional(readOnly = true)
    public AtsPageRsp<JobPositionRes> findAll(AtsPageQuery query) {
        // List<JobPosition> positions = jobPositionRepository.findByStatus(JobStatus.DRAFT);
        // return JobPositionRes.fromList(positions);
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        boolean isAdmin = jobPositionAccessService.isUserInJobPositionAdminRole(loggedInUser);
        Page<JobPosition> page = isAdmin 
            ? jobPositionRepository.findAllForTenantAdmin(AuthService.getLoggedInTenantId(), query.toPageable()) 
            : jobPositionRepository.findAllForTenantNonAdmin(AuthService.getLoggedInTenantId(), loggedInUser.getUserId(), loggedInUser.getPublicId(), query.toPageable());
        return AtsPageRsp.of(page.map(JobPositionRes::from));
    }

    /**
     * Get all interview plans for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @return
     */
    @Transactional(readOnly = true)
    public List<JobPositionRes> findAllAsList() {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        boolean isAdmin = jobPositionAccessService.isUserInJobPositionAdminRole(loggedInUser);
        List<JobPosition> positions = isAdmin 
            ? jobPositionRepository.findAllForTenantAdminAsList(AuthService.getLoggedInTenantId()) 
            : jobPositionRepository.findAllForTenantNonAdminAsList(AuthService.getLoggedInTenantId(), loggedInUser.getUserId(), loggedInUser.getPublicId());
        return positions.stream().map(JobPositionRes::from).toList();
    }

    public AtsPageRsp<JobPosition> listJobPosition(AtsPageQuery query) {
        Page<JobPosition> page = jobPositionRepository.findAll(query.toPageable());
        return AtsPageRsp.of(page);
    }

    public Optional<JobPosition> findById(Long id) {
        return jobPositionRepository.findById(id);
    }

    public JobFiltersData getFiltersData() {
        return JobFiltersData.from(
            jobEmploymentTypeRepository.findAll(),
            jobWorkArrangementTypeRepository.findAll(),
            jobExperienceLevelTypeRepository.findAll(),
            evaluationPlanService.findAllAsList(),
            recruitmentPipelineService.findAllAsList(),
            screeningQuestionsFormService.findAllAsList(),
            userService.findActiveByRolesForTenant(Set.of(RoleType.RECRUITER, RoleType.HR_ADMIN)),
            userService.findActiveByRolesForTenant(Set.of(RoleType.HIRING_MANAGER, RoleType.HR_ADMIN, RoleType.RECRUITER)),
            evaluationPlanService.findAllRoundTypes(),
            recruitmentPipelineService.findAllRoundTypes(),
            screeningQuestionsFormService.findAllScreeningQuestionTypes()
        );
    }

}
