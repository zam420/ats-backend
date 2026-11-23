package com.deepconnect.ats.job.service;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.service.EnumsResService;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.job.dto.JobFiltersRes;
import com.deepconnect.ats.job.dto.JobPositionSearchQuery;
import com.deepconnect.ats.job.dto.JobPositionRes;
import com.deepconnect.ats.job.dto.JobPositionSummaryResForCandidate;
import com.deepconnect.ats.job.model.JobEvaluationPlanRound;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.job.model.JobScreeningQuestion;
import com.deepconnect.ats.job.security.JobRichTextSanitizer;
import com.deepconnect.ats.job.repo.JobPositionRepository;
import com.deepconnect.ats.job.repo.JobPositionSearchSpecification;
import com.deepconnect.ats.job.repo.JobEvaluationPlanRoundRepository;
import com.deepconnect.ats.evaluationplan.enums.EvaluationPlanRoundType;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanRoundHelper;
import com.deepconnect.ats.evaluationplan.service.EvaluationPlanService;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineService;
import com.deepconnect.ats.recruitmentpipeline.service.RecruitmentPipelineRoundHelper;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.jobapplication.repo.JobApplicationRepository;
import com.deepconnect.ats.jobapplication.service.JobApplicationService;
import com.deepconnect.ats.user.service.UserServiceV2;
import com.deepconnect.ats.rbac.enums.RoleType;
import com.deepconnect.ats.job.dto.JobPositionReq;
import com.deepconnect.ats.job.repo.JobRecruitmentPipelineRoundRepository;
import com.deepconnect.ats.job.repo.JobScreeningQuestionRepository;
import com.deepconnect.ats.job.dto.JobPositionDetailsRes;
import com.deepconnect.ats.job.dto.JobPositionCandidateRes;
import com.deepconnect.ats.screeningquestionsform.enums.ScreeningQuestionType;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionHelper;
import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.common.utils.Validation;
import com.deepconnect.ats.common.enums.ValidationFieldType;
import com.deepconnect.ats.screeningquestionsform.service.ScreeningQuestionsFormService;
import com.deepconnect.ats.job.dto.JobFiltersResForCandidate;
import com.deepconnect.ats.tenant.service.TenantLogoStorage;
import com.deepconnect.ats.job.enums.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Comparator;
import java.util.ArrayList;
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

    @Value("${screeningQuestionsForms.maxControls}")
    private int maxQuestions;

    @Value("${screeningQuestionsForms.rtLabelMaxLength}")
    private Integer rtLabelMaxLength;

    @Value("${screeningQuestionsForms.nonRTLabelMaxLength}")
    private Integer nonRTLabelMaxLength;

    @Value("${jobPosition.shortDescriptionMaxWords}")
    private int shortDescriptionMaxWords;

    @Value("${ats.logo.public-base-url:}")
    private String publicLogoBaseUrl = "";

    private final EvaluationPlanService evaluationPlanService;
    private final RecruitmentPipelineService recruitmentPipelineService;
    private final JobPositionAccessService jobPositionAccessService;
    private final ScreeningQuestionsFormService screeningQuestionsFormService;
    private final JobApplicationService jobApplicationService;
    private final UserServiceV2 userService;
    private final EnumsResService enumsResService;

    private final JobPositionRepository jobPositionRepository;
    private final JobEvaluationPlanRoundRepository jobEvaluationPlanRoundRepository;
    private final JobRecruitmentPipelineRoundRepository jobRecruitmentPipelineRoundRepository;
    private final JobScreeningQuestionRepository jobScreeningQuestionRepository;
    private final JobApplicationRepository jobApplicationRepository;

    private String save(JobPosition position, JobPositionReq req, boolean evaluationPlanRoundsChanged, boolean recruitmentPipelineRoundsChanged, boolean screeningQuestionsChanged){
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        position.setModifiedByUserId(loggedInUser.getUserId());
        position.setModifiedByUserPublicId(loggedInUser.getPublicId());
        
        position.setTitle(req.getTitle());
        position.setDescription(JobRichTextSanitizer.sanitize(req.getDescription()));
        position.setShortDescription(JobRichTextSanitizer.shortDescription(position.getDescription(), shortDescriptionMaxWords));
        position.setWorkLocation(req.getWorkLocation());
        position.setRequisitionId(req.getRequisitionId());
        position.setNumOfOpenings(req.getNumOfOpenings());
        position.setOffice(req.getOffice());
        position.setDepartment(req.getDepartment());
        position.setRecruiters(req.getRecruiters());
        position.setHiringManagers(req.getHiringManagers());
        position.setEmploymentType(enumsResService.resolve(req.getEmploymentType(), JobEmploymentType.values()));
        position.setWorkArrangementType(enumsResService.resolve(req.getWorkArrangementType(), JobWorkArrangementType.values()));
        position.setExperienceLevelType(enumsResService.resolve(req.getExperienceLevelType(), JobExperienceLevelType.values()));
        position.setApplicationQuestionsDisplayMode(enumsResService.resolve(req.getApplicationQuestionsDisplayMode(), JobQuestionsDisplayMode.values()));

        position = jobPositionRepository.save(position);
        if (evaluationPlanRoundsChanged){
            Validation.validateMustAdd(ValidationFieldType.EVALUATION_PLAN_ROUND, req.getEvaluationPlanRounds());
            if (req.getEvaluationPlanRounds().size() > maxRounds) {
                throw new InternalServerException(String.format("Too many rounds. User: %s. Plan: %s. Rounds: %d", loggedInUser.getPublicId(), position.getPublicId(), req.getEvaluationPlanRounds().size()));
            }
            for (JobEvaluationPlanRound roundTemplate : req.getEvaluationPlanRounds()) {
                roundTemplate.setJobPosition(position);
                roundTemplate.setJobPositionId(position.getId());
                if(!StringUtils.hasText(roundTemplate.getName())) {
                    roundTemplate.setName(enumsResService.from(roundTemplate.getRoundType()).getName());
                }
            }
            jobEvaluationPlanRoundRepository.saveAll(req.getEvaluationPlanRounds());
        }

        if (recruitmentPipelineRoundsChanged) {
            Validation.validateMustAdd(ValidationFieldType.RECRUITMENT_PIPELINE_STAGE, req.getRecruitmentPipelineRounds());
            if (req.getRecruitmentPipelineRounds().size() > maxRounds) {
                throw new InternalServerException(String.format("Too many rounds. User: %s. Position: %s. Rounds: %d", loggedInUser.getPublicId(), position.getPublicId(), req.getRecruitmentPipelineRounds().size()));
            }
            RecruitmentPipelineRoundHelper.assertMandatoryRoundsPresent(req.getRecruitmentPipelineRounds(), loggedInUser.getPublicId(), position.getPublicId());
            RecruitmentPipelineRoundHelper.assertRoundTypeOrder(req.getRecruitmentPipelineRounds(), loggedInUser.getPublicId(), position.getPublicId());
            assertAppliedFirst(req.getRecruitmentPipelineRounds());
            for (JobRecruitmentPipelineRound roundTemplate : req.getRecruitmentPipelineRounds()) {
                roundTemplate.setJobPosition(position);
                roundTemplate.setJobPositionId(position.getId());
                if(!StringUtils.hasText(roundTemplate.getName())) {
                    roundTemplate.setName(enumsResService.from(roundTemplate.getRoundType()).getName());
                }
            }
            jobRecruitmentPipelineRoundRepository.saveAll(req.getRecruitmentPipelineRounds());
        }

        if (screeningQuestionsChanged && !CollectionUtils.isEmpty(req.getScreeningQuestions())) {
            if (req.getScreeningQuestions().size() > maxQuestions) {
                throw new InternalServerException(String.format("Too many questions. User: %s. Position: %s. Questions: %d", loggedInUser.getPublicId(), position.getPublicId(), req.getScreeningQuestions().size()));
            }
            ScreeningQuestionHelper.sanitizeLabels(req.getScreeningQuestions());
            ScreeningQuestionHelper.assertMaxLength(req.getScreeningQuestions(), rtLabelMaxLength, nonRTLabelMaxLength);
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
        JobPosition jobPosition = jobPositionRepository.findForUpdate(req.getId(), loggedInUser.getTenantId())
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

        boolean recruitmentPipelineRoundsChanged = !RecruitmentPipelineRoundHelper.roundsEqual(jobPosition.getRecruitmentPipelineRounds(), req.getRecruitmentPipelineRounds())
            || !sameStageIdentities(jobPosition.getRecruitmentPipelineRounds(), req.getRecruitmentPipelineRounds());
        if (recruitmentPipelineRoundsChanged) {
            updateRecruitmentPipeline(jobPosition, req.getRecruitmentPipelineRounds(), loggedInUser);
        }

        boolean screeningQuestionsChanged = !ScreeningQuestionHelper.questionsEqual(jobPosition.getScreeningQuestions(), req.getScreeningQuestions());
        if (screeningQuestionsChanged) {
           jobScreeningQuestionRepository.archiveByJobPositionId(jobPosition.getId());
        }

        return this.save(jobPosition, req, evaluationPlanRoundsChanged, false, screeningQuestionsChanged);
    }

    /*Asserts that the first recruitment pipeline round is of type APPLIED and that the rounds are valid.*/
    private void assertAppliedFirst(List<JobRecruitmentPipelineRound> rounds) {
        if (rounds == null || rounds.isEmpty() || rounds.stream().anyMatch(round -> round.getOrder() == null)
                || rounds.stream().anyMatch(round -> round.getOrder() < 0 || round.getRoundType() == null)
                || rounds.stream().map(JobRecruitmentPipelineRound::getOrder).distinct().count() != rounds.size()
                || rounds.stream().min(Comparator.comparing(JobRecruitmentPipelineRound::getOrder))
                    .orElseThrow().getRoundType() != RecruitmentPipelineRoundType.APPLIED) {
            throw new CustomException(CustomExceptionType.JOB_PIPELINE_INVALID);
        }
    }

    /*Checks whether the identities of recruitment pipeline stages (rounds) remain unchanged between an existing list and an incoming list, matching stages by their order.*/
    private boolean sameStageIdentities(List<JobRecruitmentPipelineRound> existing, List<JobRecruitmentPipelineRound> incoming) {
        if (existing == null || incoming == null || incoming.stream().noneMatch(stage -> stage.getPublicId() != null)) return true;
        if (existing.size() != incoming.size()) return false;
        Map<Integer, String> idsByOrder = new HashMap<>();
        existing.forEach(stage -> idsByOrder.put(stage.getOrder(), stage.getPublicId()));
        return incoming.stream().allMatch(stage -> stage.getPublicId() != null
                && stage.getPublicId().equals(idsByOrder.get(stage.getOrder())));
    }

    private void updateRecruitmentPipeline(JobPosition position, List<JobRecruitmentPipelineRound> incoming, LoggedInUserDetails user) {
        Validation.validateMustAdd(ValidationFieldType.RECRUITMENT_PIPELINE_STAGE, incoming);
        if (incoming.size() > maxRounds) {
            throw new InternalServerException("Too many recruitment pipeline stages");
        }
        assertAppliedFirst(incoming);
        RecruitmentPipelineRoundHelper.assertMandatoryRoundsPresent(incoming, user.getPublicId(), position.getPublicId());
        RecruitmentPipelineRoundHelper.assertRoundTypeOrder(incoming, user.getPublicId(), position.getPublicId());
        List<JobRecruitmentPipelineRound> existing = jobRecruitmentPipelineRoundRepository.findByJobPositionIdOrderByOrderAsc(position.getId());
        Map<String, JobRecruitmentPipelineRound> byPublicId = new HashMap<>();
        existing.forEach(stage -> byPublicId.put(stage.getPublicId(), stage));

        // Track existing stage IDs referenced by the incoming pipeline. This also allows us to identify stages that have been removed.
        Set<String> used = new HashSet<>();
        for (JobRecruitmentPipelineRound stage : incoming) {
            if (stage.getPublicId() == null) continue;
            JobRecruitmentPipelineRound old = byPublicId.get(stage.getPublicId());// Resolve the incoming public ID against the currently persisted stages.
            // Reject the update if:
            // - The supplied ID does not belong to this job position (stale or invalid ID).
            // - The same existing stage ID appears more than once in the incoming payload.
            if (old == null || !used.add(stage.getPublicId())) {
                throw new CustomException(CustomExceptionType.JOB_PIPELINE_STALE);
            }
        }

        // Check whether any existing stages omitted from the incoming pipeline are still referenced by non-rejected job applications.
        // Such stages cannot be removed because active applications depend on them.
        for (JobRecruitmentPipelineRound old : existing) {
            if (!used.contains(old.getPublicId()) && jobApplicationService.existsByRecruitmentPipelineStageAndRejectedFalse(old.getId())) {
                throw new CustomException(CustomExceptionType.JOB_PIPELINE_OCCUPIED_STAGE);
            }
        }
        /*When a candidate was rejected in a custom stage, and if that stage is being deleted, thenclear the stage reference from job applications table. 
        When a pipeline stage is removed, its foreign key would otherwise prevent deletion.
        The rejection stage’s name and type are saved separately as snapshots, so they remain visible in the Rejected table. 
        Stages containing active candidates cannot be removed. */
        for (int i = 0; i < existing.size(); i++) existing.get(i).setOrder(-i - 1);
        jobRecruitmentPipelineRoundRepository.saveAllAndFlush(existing);
        List<JobRecruitmentPipelineRound> removed = existing.stream().filter(stage -> !used.contains(stage.getPublicId())).toList();
        if (!removed.isEmpty()) {
            jobApplicationService.clearRejectedStages(removed.stream().map(JobRecruitmentPipelineRound::getId).toList());
        }
        position.getRecruitmentPipelineRounds().removeAll(removed);
        jobRecruitmentPipelineRoundRepository.flush();
        List<JobRecruitmentPipelineRound> updated = new ArrayList<>();
        for (JobRecruitmentPipelineRound stage : incoming) {
            JobRecruitmentPipelineRound target = stage.getPublicId() == null ? new JobRecruitmentPipelineRound() : byPublicId.get(stage.getPublicId());
            target.setJobPosition(position);
            target.setJobPositionId(position.getId());
            target.setRoundType(stage.getRoundType());
            target.setName(StringUtils.hasText(stage.getName()) ? stage.getName() : enumsResService.from(stage.getRoundType()).getName());
            target.setDescription(stage.getDescription());
            target.setOrder(stage.getOrder());
            updated.add(target);
            if (stage.getPublicId() == null) position.getRecruitmentPipelineRounds().add(target);
        }
        jobRecruitmentPipelineRoundRepository.saveAll(updated);
    }

    @Transactional
    public void publish(String publicId) {
        JobPosition jobPosition = jobPositionRepository.findForUpdate(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
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
        JobPosition jobPosition = jobPositionRepository.findForUpdate(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
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
        JobPosition jobPosition = jobPositionRepository.findForUpdate(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
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

    @Transactional(readOnly = true)
    public JobPositionDetailsRes findByPublicIdForTenant(String publicId) {
        JobPosition position = jobPositionRepository.findByPublicIdAndTenantIdAndDeactivatedFalse(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
        jobPositionAccessService.assertReadAccess(AuthService.getLoggedInUser(), position);
        return JobPositionDetailsRes.from(position);
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<JobPositionRes> findAll(JobPositionSearchQuery query) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        boolean isAdmin = jobPositionAccessService.isUserInJobPositionAdminRole(loggedInUser);
        Page<JobPosition> page = jobPositionRepository.findAll(
            JobPositionSearchSpecification.forTenantUser(query, loggedInUser.getTenantId(),
                loggedInUser.getUserId(), loggedInUser.getPublicId(), isAdmin), query.toPageable());
        Map<Long, Long> counts = new HashMap<>();
        if (!page.isEmpty()) {
            jobApplicationRepository.countForPositions(page.getContent().stream().map(JobPosition::getId).toList())
                .forEach(count -> counts.put(count.getJobPositionId(), count.getApplicationCount()));
        }
        
        HashSet<Long> ownerIds = new HashSet<>();
        for(JobPosition position : page.getContent()) {
            ownerIds.add(position.getOwnerId());
        }
        HashMap<Long, String> ownerIdToUser = userService.findNameByUserIds(ownerIds);
        return AtsPageRsp.of(page.map(position -> {
            JobPositionRes res = JobPositionRes.from(position);
            res.setApplicationCount(counts.getOrDefault(position.getId(), 0L));
            res.setOwnerName(ownerIdToUser.getOrDefault(position.getOwnerId(), ""));
            return res;
        }));
    }

    /**
     * Get all interview plans for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @return
     */
    // @Transactional(readOnly = true)
    // public List<JobPositionRes> findAllAsList() {
    //     LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
    //     boolean isAdmin = jobPositionAccessService.isUserInJobPositionAdminRole(loggedInUser);
    //     List<JobPosition> positions = isAdmin 
    //         ? jobPositionRepository.findAllForTenantAdminAsList(AuthService.getLoggedInTenantId()) 
    //         : jobPositionRepository.findAllForTenantNonAdminAsList(AuthService.getLoggedInTenantId(), loggedInUser.getUserId(), loggedInUser.getPublicId());
    //     return positions.stream().map(JobPositionRes::from).toList();
    // }

    // public AtsPageRsp<JobPosition> listJobPosition(AtsPageQuery query) {
    //     Page<JobPosition> page = jobPositionRepository.findAll(query.toPageable());
    //     return AtsPageRsp.of(page);
    // }

    public Optional<JobPosition> findById(Long id) {
        return jobPositionRepository.findById(id);
    }

    public JobFiltersRes getFilters() {
        return JobFiltersRes.from(
            enumsResService.from(JobEmploymentType.values()),
            enumsResService.from(JobWorkArrangementType.values()),
            enumsResService.from(JobExperienceLevelType.values()),
            enumsResService.from(JobStatus.values()),
            enumsResService.from(JobQuestionsDisplayMode.values()),
            enumsResService.from(JobPostedDateType.values()),
            evaluationPlanService.findAllAsList(),
            recruitmentPipelineService.findAllAsList(),
            screeningQuestionsFormService.findAllAsList(),
            userService.findActiveByRolesForTenant(Set.of(RoleType.RECRUITER, RoleType.HR_ADMIN)),
            userService.findActiveByRolesForTenant(Set.of(RoleType.HIRING_MANAGER, RoleType.HR_ADMIN, RoleType.RECRUITER)),
            enumsResService.from(EvaluationPlanRoundType.values()),
            enumsResService.from(RecruitmentPipelineRoundType.values()),
            enumsResService.from(ScreeningQuestionType.values())
        );
    }

    public JobFiltersResForCandidate getFiltersForCandidates() {
        return JobFiltersResForCandidate.from(
            enumsResService.from(JobEmploymentType.values()),
            enumsResService.from(JobWorkArrangementType.values()),
            enumsResService.from(JobExperienceLevelType.values()),
            enumsResService.from(JobPostedDateType.values()),
            enumsResService.from(ScreeningQuestionType.values())
        );
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<JobPositionSummaryResForCandidate> findAllForCandidate(JobPositionSearchQuery query) {
        Page<JobPosition> page = jobPositionRepository.findAll(
            JobPositionSearchSpecification.forCandidate(query), query.toPageable());
        return AtsPageRsp.of(page.map(position -> {
            JobPositionSummaryResForCandidate result = JobPositionSummaryResForCandidate.from(position);
            result.setCompanyLogoUrl(TenantLogoStorage.publicUrl(position.getTenant().getLogoPath(), publicLogoBaseUrl));
            return result;
        }));
    }

    @Transactional(readOnly = true)
    public JobPositionCandidateRes findByPublicIdForCandidate(String publicId) {
        JobPosition position = findOpenPositionForCandidate(publicId);
        JobPositionCandidateRes result = JobPositionCandidateRes.from(position);
        result.setCompanyLogoUrl(TenantLogoStorage.publicUrl(position.getTenant().getLogoPath(), publicLogoBaseUrl));
        return result;
    }

    @Transactional(readOnly = true)
    public String findLogoForCandidate(String publicId) {
        return findOpenPositionForCandidate(publicId).getTenant().getLogoPath();
    }

    private JobPosition findOpenPositionForCandidate(String publicId) {
        return jobPositionRepository.findByPublicIdAndStatusAndDeactivatedFalse(publicId, JobStatus.OPEN)
            .orElseThrow(() -> new InternalServerException(String.format("Job position not found: User: %s, Position: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
    }

}
