package com.deepconnect.ats.recruitmentpipeline.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineReq;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.recruitmentpipeline.repo.RecruitmentPipelineRepository;
import com.deepconnect.ats.recruitmentpipeline.repo.RecruitmentPipelineShareRepository;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.model.JobRecruitmentPipelineRound;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRes;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineDetailsRes;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundBase;
import com.deepconnect.ats.recruitmentpipeline.enums.RecruitmentPipelineRoundType;
import com.deepconnect.ats.recruitmentpipeline.repo.RecruitmentPipelineRoundRepository;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.user.service.UserServiceV2;
import com.deepconnect.ats.common.dto.TemplateFiltersRes;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.common.utils.HashMapUtils;
import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.common.utils.Validation;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineShare;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.enums.ValidationFieldType;
import com.deepconnect.ats.common.service.EnumsResService;
import com.deepconnect.ats.common.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentPipelineService {

    @Value("${plans.maxRounds}")
    private int maxRounds;

    private final EnumsResService enumsResService;
    private final UserServiceV2 userService;
    private final RecruitmentPipelineAccessService recruitmentPipelineAccessService;

    private final RecruitmentPipelineRepository recruitmentPipelineRepository;
    private final RecruitmentPipelineRoundRepository recruitmentPipelineRoundRepository;
    private final RecruitmentPipelineShareRepository recruitmentPipelineShareRepository;

    private String save(RecruitmentPipeline plan, RecruitmentPipelineReq req, boolean roundsChanged, boolean sharedWithUsersChanged, boolean isPublicForTenant, boolean isPublicForAll) {
        Validation.validateMustAdd(ValidationFieldType.RECRUITMENT_PIPELINE_STAGE, req.getRounds());
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        plan.setModifiedByUserId(loggedInUser.getUserId());
        plan.setModifiedByUserPublicId(loggedInUser.getPublicId());
        plan.setName(req.getName());
        plan.setDescription(req.getDescription());
        plan.setIsPublicForTenant(isPublicForTenant);
        plan.setIsPublicForAll(isPublicForAll);
        if(sharedWithUsersChanged){
            plan.setIsSharedWithinTenant(!CollectionUtils.isEmpty(req.getSharedWithUserIds()));
        }
        plan = recruitmentPipelineRepository.save(plan);
        if (roundsChanged && !CollectionUtils.isEmpty(req.getRounds())) {
            if (req.getRounds().size() > maxRounds) {
                throw new InternalServerException(String.format("Too many rounds. User: %s. Plan: %s. Rounds: %d", loggedInUser.getPublicId(), plan.getPublicId(), req.getRounds().size()));
            }
            List<RecruitmentPipelineRound> rounds = RecruitmentPipelineRound.from(req.getRounds(), plan.getId());
            RecruitmentPipelineRoundHelper.validate(rounds, loggedInUser.getPublicId(), plan.getPublicId());
            for (RecruitmentPipelineRound round : rounds) {
                if(!StringUtils.hasText(round.getName())) {
                    round.setName(enumsResService.from(round.getRoundType()).getName());
                }
            }
            recruitmentPipelineRoundRepository.saveAll(rounds);
        }

        if(sharedWithUsersChanged) {
            if(!CollectionUtils.isEmpty(plan.getSharedWithUsers())) {
                recruitmentPipelineShareRepository.deleteByRecruitmentPipelineId(plan.getId());
            }
            if(!CollectionUtils.isEmpty(req.getSharedWithUserIds())){
                Set<User> sharedWithUsers = userService.findByPublicIdInAndDeactivatedFalseAndLockedFalse(req.getSharedWithUserIds());
                List<RecruitmentPipelineShare> sharedWithUserList = new ArrayList<>();
                for(User user : sharedWithUsers) {
                    RecruitmentPipelineShare sharedWithUser = new RecruitmentPipelineShare();
                    sharedWithUser.setRecruitmentPipelineId(plan.getId());
                    sharedWithUser.setRecruitmentPipelinePublicId(plan.getPublicId());
                    sharedWithUser.setUserId(user.getId());
                    sharedWithUser.setUserPublicId(user.getPublicId());
                    sharedWithUserList.add(sharedWithUser);
                }
                recruitmentPipelineShareRepository.saveAll(sharedWithUserList);
            }
        }

        return plan.getPublicId();
    }

    @Transactional
    public String create(RecruitmentPipelineReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForCreation(loggedInUser.getTenantId(), req.getName());
        RecruitmentPipeline plan = new RecruitmentPipeline();
        plan.setCreatedByUserId(loggedInUser.getUserId());
        plan.setCreatedByUserPublicId(loggedInUser.getPublicId());
        plan.setTenantId(loggedInUser.getTenantId());
        if(req.getOwnerId() == null || req.getOwnerId().equals(loggedInUser.getPublicId())) {
            plan.setOwnerId(loggedInUser.getUserId());
            plan.setOwnerPublicId(loggedInUser.getPublicId());
            plan.setOwnerName(loggedInUser.getUsername());
        }
        else {
            setAnotherUserAsOwner(plan, recruitmentPipelineAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        }

        return this.save(plan, req, true, !CollectionUtils.isEmpty(req.getSharedWithUserIds()), req.getIsPublic() == null ? false : req.getIsPublic(), false  );
    }

    @Transactional
    public String update(RecruitmentPipelineReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        RecruitmentPipeline plan = recruitmentPipelineRepository.findByPublicIdAndTenantIdAndDeactivatedFalse(req.getId(), AuthService.getLoggedInTenantId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForUpdate(loggedInUser.getTenantId(), req.getName(), plan.getId());
        recruitmentPipelineAccessService.assertWriteAccess(loggedInUser, plan);
        if(req.getOwnerId() != null && !loggedInUser.getPublicId().equals(req.getOwnerId())){//if the requested owner is not the logged in user, additional checks are needed
            setAnotherUserAsOwner(plan, recruitmentPipelineAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        }

        List<RecruitmentPipelineRound> rounds = RecruitmentPipelineRound.from(req.getRounds(), plan.getId());
        boolean roundsChanged = !RecruitmentPipelineRoundHelper.roundsEqual(plan.getRounds(), rounds);
        if (roundsChanged) {
            recruitmentPipelineRoundRepository.deleteByRecruitmentPipelineId(plan.getId());
        }

        boolean isPublicForTenant = req.getIsPublic() == null ? false : req.getIsPublic();
        boolean sharedWithUsersChanged = false;
        //if previously plan was shared with specific users, but now it's shared with Everyone or None
        if(!CollectionUtils.isEmpty(plan.getSharedWithUsers()) && (isPublicForTenant || CollectionUtils.isEmpty(req.getSharedWithUserIds()))) {
            sharedWithUsersChanged = true;
        }
        else if(!CollectionUtils.isEmpty(req.getSharedWithUserIds())) { //if the request has specific users to share with
            sharedWithUsersChanged = !sharedWithUsersEqual(plan.getSharedWithUsers(), req.getSharedWithUserIds());
        }

        return this.save(plan, req, roundsChanged, sharedWithUsersChanged, isPublicForTenant, false);
    }

    /**
     * Creates a new recruitment pipeline system template.
     * The difference between this and a regular create are:
     * 1. This method calls assertUniqueWithinAll while a regular update calls assertUniqueWithinTenant.
     * 2. This method doesn't allow setting the owner of the evaluation plan, unlike a regular update. It automatically sets the owner to current user.
     * 3. This method always sets isPublicForTentant to false and isPublicForAll to true.
     * @param req
     * @return
     */
    @Transactional
    public String createSystemTemplate(RecruitmentPipelineReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForCreation(req.getName());
        RecruitmentPipeline plan = new RecruitmentPipeline();
        plan.setCreatedByUserId(loggedInUser.getUserId());
        plan.setCreatedByUserPublicId(loggedInUser.getPublicId());
        plan.setTenantId(loggedInUser.getTenantId());
        plan.setOwnerId(loggedInUser.getUserId());
        plan.setOwnerPublicId(loggedInUser.getPublicId());
        plan.setOwnerName(loggedInUser.getUsername());
        return this.save(plan, req, true, false, false, true);
    }

    /**
     * Updates an existing recruitment pipeline system template.
     * The difference between this and a regular update are:
     * 1. This method calls assertUniqueWithinAll while a regular update calls assertUniqueWithinTenant.
     * 2. This method doesn't allow changing the owner of the evaluation plan, unlike a regular update.
     * 3. This method always sets isPublicForTentant to false and isPublicForAll to true.
     * @param req
     * @return
     */
    @Transactional
    public String updateSystemTemplate(RecruitmentPipelineReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        RecruitmentPipeline plan = recruitmentPipelineRepository.findByDeactivatedFalseAndPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForUpdate(req.getName(), plan.getId());
        recruitmentPipelineAccessService.assertWriteAccess(loggedInUser, plan);
        List<RecruitmentPipelineRound> rounds = RecruitmentPipelineRound.from(req.getRounds(), plan.getId());
        boolean roundsChanged = !RecruitmentPipelineRoundHelper.roundsEqual(plan.getRounds(), rounds);
        if (roundsChanged) {
            recruitmentPipelineRoundRepository.deleteByRecruitmentPipelineId(plan.getId());
        }

        return this.save(plan, req, roundsChanged, false, false, true);
    }

    @Transactional
    public void deactivate(String publicId) {
        RecruitmentPipeline plan = recruitmentPipelineRepository.findByDeactivatedFalseAndPublicId(publicId).orElseThrow(() -> new InternalServerException("Interview Process not found: " + publicId));
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        recruitmentPipelineAccessService.assertWriteAccess(loggedInUser, plan);
        plan.setDeactivated(true);
        plan.setModifiedByUserId(loggedInUser.getUserId());
        plan.setModifiedByUserPublicId(loggedInUser.getPublicId());
        recruitmentPipelineRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public RecruitmentPipelineDetailsRes findByPublicIdForTenant(String publicId) {
        //RecruitmentPipeline plan = recruitmentPipelineRepository.findByPublicIdAndTenantIdAndDeactivatedFalse(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException("Evaluation plan not found: " + publicId));
        RecruitmentPipeline plan = recruitmentPipelineRepository.findByPublicIdAndDeactivatedFalse(publicId).orElseThrow(() -> new InternalServerException("Evaluation plan not found: " + publicId));
        recruitmentPipelineAccessService.assertReadAccess(AuthService.getLoggedInUser(), plan);
        return RecruitmentPipelineDetailsRes.from(plan);
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<RecruitmentPipelineRes> findAll(AtsPageQuery query) {
        boolean isAdmin = recruitmentPipelineAccessService.isUserInRecruitmentPipelineAdminRole(AuthService.getLoggedInUser());
        Page<RecruitmentPipeline> page = isAdmin 
            ? recruitmentPipelineRepository.findAllForTenantAdmin(AuthService.getLoggedInTenantId(), query.toPageable()) 
            : recruitmentPipelineRepository.findAllForTenantNonAdmin(AuthService.getLoggedInTenantId(), AuthService.getLoggedInUser().getUserId(), query.toPageable());
        return this.getResponseFromPage(page);
    }

    /**
     * Get all interview plans for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @return
     */
    @Transactional(readOnly = true)
    public List<RecruitmentPipelineRes> findAllAsList() {
        boolean isAdmin = recruitmentPipelineAccessService.isUserInRecruitmentPipelineAdminRole(AuthService.getLoggedInUser());
        List<RecruitmentPipeline> plans = isAdmin 
            ? recruitmentPipelineRepository.findAllForTenantAdminAsList(AuthService.getLoggedInTenantId()) 
            : recruitmentPipelineRepository.findAllForTenantNonAdminAsList(AuthService.getLoggedInTenantId(), AuthService.getLoggedInUser().getUserId());
        return plans.stream().map(RecruitmentPipelineRes::from).toList();
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<RecruitmentPipelineRes> findAllSystemTemplates(AtsPageQuery query) {
        return this.getResponseFromPage(recruitmentPipelineRepository.findByDeactivatedFalseAndIsPublicForAllTrue(query.toPageable()));
    }

    /**
     * Get all recruitment pipelines that are shared with current user or shared with all users
     * Only accessible to Recruitment Pipeline Admins (permission code: rpa).
     * @param query the pagination and status filter query
     * @return a paginated response containing recruitment pipelines for the tenant
     */
    @Transactional(readOnly = true)
    public AtsPageRsp<RecruitmentPipelineRes> findAllSharedWithUser(AtsPageQuery query) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        return this.getResponseFromPage(recruitmentPipelineRepository.findPublicOrSharedForTenant(loggedInUser.getTenantId(), loggedInUser.getUserId(), query.toPageable()));
    }

    /**
     * Get all recruitment pipelines for the specified plan owner. 
     * Only accessible to Recruitment Pipeline Admins (permission code: rpa).
     * @param query the pagination and status filter query
     * @return a paginated response containing recruitment pipelines for the tenant
     */
    @Transactional(readOnly = true)
    public AtsPageRsp<RecruitmentPipelineRes> findAllForPlanOwner(AtsPageQuery query) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        return this.getResponseFromPage(recruitmentPipelineRepository.findByDeactivatedFalseAndTenantIdAndOwnerId(loggedInUser.getTenantId(), loggedInUser.getUserId(), query.toPageable()));
    }

    /**
     * Get all recruitment pipelines for logged in user's tenant. 
     * Only accessible to Recruitment Pipeline Admins (permission code: rpa).
     * @param query the pagination and status filter query
     * @return a paginated response containing recruitment pipelines for the tenant
     */
    private AtsPageRsp<RecruitmentPipelineRes> findAllForTenant(AtsPageQuery query) {
        return this.getResponseFromPage(recruitmentPipelineRepository.findByDeactivatedFalseAndTenantId(AuthService.getLoggedInTenantId(), query.toPageable()));
    }

    private void setAnotherUserAsOwner(RecruitmentPipeline recruitmentPipeline, AccessType loggedInUserAccessType, String ownerId) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        if(loggedInUserAccessType != AccessType.TENANT_ADMIN && loggedInUserAccessType != AccessType.SYSTEM_ADMIN) {
            throw new InternalServerException("Attempt to set another user as owner without permission by " + loggedInUser.getPublicId());
        }

        User user = userService.findByPublicIdAndDeactivatedFalseAndLockedFalse(ownerId).orElseThrow(() -> new InternalServerException("Owner not found: " + ownerId));
        if(!user.getTenantId().equals(loggedInUser.getTenantId())) {
            throw new InternalServerException("Owner must belong to the same tenant as the logged in user. Owner: " + ownerId + ", Request by: " + loggedInUser.getPublicId());
        }
        recruitmentPipeline.setOwnerId(user.getId());
        recruitmentPipeline.setOwnerPublicId(user.getPublicId());
        recruitmentPipeline.setOwnerName(user.getName());
    }

    /**
     * Checks if the existing shared users are equal to the incoming set of user IDs.
     * @param existing the list of existing shared users
     * @param userIdsIncoming the set of incoming user IDs to compare against
     * @return true if the existing shared users match the incoming set of user IDs, false otherwise
     */
    private boolean sharedWithUsersEqual(List<RecruitmentPipelineShare> existing, Set<String> userIdsIncoming) {
        List<RecruitmentPipelineShare> a = existing == null ? List.of() : existing;
        if (a.size() != userIdsIncoming.size())
            return false;
        for (RecruitmentPipelineShare planSharedWithUser : a) {
            if (!userIdsIncoming.contains(planSharedWithUser.getUserPublicId()))
                return false;
        }
        
        return true;
    }

    private void assertUniqueWithinTenantForCreation(Long tenantId, String name) {
        if(recruitmentPipelineRepository.existsByDeactivatedFalseAndTenantIdAndName(tenantId, name)) {
            throw new CustomException(CustomExceptionType.DUPLICATE_NAME_WITHIN_TENANT, "recruitment plan");
        }
    }

    private void assertUniqueWithinTenantForUpdate(Long tenantId, String name, Long planId) {
        if(recruitmentPipelineRepository.existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(tenantId, name, planId)) {
            throw new CustomException(CustomExceptionType.DUPLICATE_NAME_WITHIN_TENANT, "recruitment plan");
        }
    }

    private void assertUniqueWithinAllForCreation(String name) {
        if(recruitmentPipelineRepository.existsByDeactivatedFalseAndName(name)) {
            throw new CustomException(CustomExceptionType.DUPLICATE_NAME, "recruitment plan");
        }
    }

    private void assertUniqueWithinAllForUpdate(String name, Long planId) {
        if(recruitmentPipelineRepository.existsByDeactivatedFalseAndNameAndIdNot(name, planId)) {
            throw new CustomException(CustomExceptionType.DUPLICATE_NAME, "recruitment plan");
        }
    }

    public Optional<RecruitmentPipeline> findByDeactivatedFalseAndPublicId(String publicId) {
        return recruitmentPipelineRepository.findByDeactivatedFalseAndPublicId(publicId);
    }

    public TemplateFiltersRes getFilters() {
        return TemplateFiltersRes.from(
            enumsResService.from(RecruitmentPipelineRoundType.values()),
            userService.findActiveUsersForTenant()
        );
    }

    private AtsPageRsp<RecruitmentPipelineRes> getResponseFromPage(Page<RecruitmentPipeline> page) {
        return AtsPageRsp.of(page.map(RecruitmentPipelineRes::from));
    }
}
