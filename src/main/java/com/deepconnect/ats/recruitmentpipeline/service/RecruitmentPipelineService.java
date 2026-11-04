package com.deepconnect.ats.recruitmentpipeline.service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import com.deepconnect.ats.recruitmentpipeline.repo.RecruitmentPipelineRoundTypeRepository;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineReq;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.recruitmentpipeline.repo.RecruitmentPipelineRepository;
import com.deepconnect.ats.recruitmentpipeline.repo.RecruitmentPipelineSharedWithUserRepository;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineRes;
import com.deepconnect.ats.recruitmentpipeline.dto.RecruitmentPipelineDetailsRes;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRound;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineRoundType;
import com.deepconnect.ats.recruitmentpipeline.repo.RecruitmentPipelineRoundRepository;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.user.service.UserService;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.common.utils.HashMapUtils;
import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipelineSharedWithUser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentPipelineService {

    @Value("${plans.maxRounds}")
    private int maxRounds;

    private final UserService userService;
    private final RecruitmentPipelineAccessService recruitmentPipelineAccessService;

    private final RecruitmentPipelineRepository recruitmentPipelineRepository;
    private final RecruitmentPipelineRoundRepository recruitmentPipelineRoundRepository;
    private final RecruitmentPipelineRoundTypeRepository recruitmentPipelineRoundTypeRepository;
    private final RecruitmentPipelineSharedWithUserRepository recruitmentPipelineSharedWithUserRepository;

    public List<RecruitmentPipelineRoundType> getAllRoundTypes() {
        return recruitmentPipelineRoundTypeRepository.findAll();
    }

    private String save(RecruitmentPipeline plan, RecruitmentPipelineReq req, boolean roundsChanged, boolean sharedWithUsersChanged, boolean isPublicForTenant, boolean isPublicForAll) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        plan.setModifiedByUserId(loggedInUser.getUserId());
        plan.setModifiedByUserPublicId(loggedInUser.getPublicId());
        plan.setName(req.getName());
        plan.setDescription(req.getDescription());
        plan.setIsPublicForTenant(isPublicForTenant);
        plan.setIsPublicForAll(isPublicForAll);
        plan = recruitmentPipelineRepository.save(plan);
        if (roundsChanged && req.getRounds() != null) {
            if (req.getRounds().size() > maxRounds) {
                throw new InternalServerException(String.format("Too many rounds. User: %s. Plan: %s. Rounds: %d", loggedInUser.getPublicId(), plan.getPublicId(), req.getRounds().size()));
            }
            List<RecruitmentPipelineRoundType> allRoundTypes = this.getAllRoundTypes();
            this.assertRoundTypeOrder(allRoundTypes, req.getRounds(), loggedInUser.getPublicId(), plan.getPublicId());
            HashMap<Integer, RecruitmentPipelineRoundType> roundTypesById = HashMapUtils.getMapFromList(allRoundTypes, "Id", Integer.class, RecruitmentPipelineRoundType.class);
            for (RecruitmentPipelineRound roundTemplate : req.getRounds()) {
                roundTemplate.setRecruitmentPipeline(plan);
                roundTemplate.setRecruitmentPipelineId(plan.getId());
                if(!StringUtils.hasText(roundTemplate.getName())) {
                    RecruitmentPipelineRoundType roundType = roundTypesById.get(roundTemplate.getRoundType());
                    if(roundType != null)
                        roundTemplate.setName(roundType.getName());
                }
                // recruitmentPipelineRoundRepository.save(roundTemplate);
            }
            recruitmentPipelineRoundRepository.saveAll(req.getRounds());
        }

        if(sharedWithUsersChanged) {
            if(!CollectionUtils.isEmpty(plan.getSharedWithUsers())) {
                recruitmentPipelineSharedWithUserRepository.deleteByRecruitmentPipelineId(plan.getId());
            }
            if(!CollectionUtils.isEmpty(req.getSharedWithUserIds())){
                Set<User> sharedWithUsers = userService.getByPublicIds(req.getSharedWithUserIds());
                List<RecruitmentPipelineSharedWithUser> sharedWithUserList = new ArrayList<>();
                for(User user : sharedWithUsers) {
                    RecruitmentPipelineSharedWithUser sharedWithUser = new RecruitmentPipelineSharedWithUser();
                    sharedWithUser.setRecruitmentPipelineId(plan.getId());
                    sharedWithUser.setRecruitmentPipelinePublicId(plan.getPublicId());
                    sharedWithUser.setUserId(user.getId());
                    sharedWithUser.setUserPublicId(user.getPublicId());
                    sharedWithUserList.add(sharedWithUser);
                }
                recruitmentPipelineSharedWithUserRepository.saveAll(sharedWithUserList);
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
        if(req.getOwnerId() == null) {
            plan.setOwnerId(loggedInUser.getUserId());
            plan.setOwnerPublicId(loggedInUser.getPublicId());
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

        RecruitmentPipeline plan = recruitmentPipelineRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForUpdate(loggedInUser.getTenantId(), req.getName(), plan.getId());
        recruitmentPipelineAccessService.assertWriteAccess(loggedInUser, plan);
        if(req.getOwnerId() != null && !loggedInUser.getPublicId().equals(req.getOwnerId())){//if the requested owner is not the logged in user, additional checks are needed
            setAnotherUserAsOwner(plan, recruitmentPipelineAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        }

        boolean roundsChanged = !roundsEqual(plan.getRounds(), req.getRounds());
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
    public String createTemplate(RecruitmentPipelineReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForCreation(req.getName());
        RecruitmentPipeline plan = new RecruitmentPipeline();
        plan.setCreatedByUserId(loggedInUser.getUserId());
        plan.setCreatedByUserPublicId(loggedInUser.getPublicId());
        plan.setTenantId(loggedInUser.getTenantId());
        plan.setOwnerId(loggedInUser.getUserId());
        plan.setOwnerPublicId(loggedInUser.getPublicId());
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
    public String updateTemplate(RecruitmentPipelineReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        RecruitmentPipeline plan = recruitmentPipelineRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForUpdate(req.getName(), plan.getId());
        recruitmentPipelineAccessService.assertWriteAccess(loggedInUser, plan);
        boolean roundsChanged = !roundsEqual(plan.getRounds(), req.getRounds());
        if (roundsChanged) {
            recruitmentPipelineRoundRepository.deleteByRecruitmentPipelineId(plan.getId());
        }

        return this.save(plan, req, roundsChanged, false, false, true);
    }

    @Transactional
    public boolean deactivate(String publicId) {
        RecruitmentPipeline plan = recruitmentPipelineRepository.findByPublicId(publicId).orElseThrow(() -> new InternalServerException("Interview Process not found: " + publicId));
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        recruitmentPipelineAccessService.assertWriteAccess(loggedInUser, plan);
        plan.setDeactivated(true);
        plan.setModifiedByUserId(loggedInUser.getUserId());
        plan.setModifiedByUserPublicId(loggedInUser.getPublicId());
        recruitmentPipelineRepository.save(plan);
        return true;
    }

    @Transactional(readOnly = true)
    public RecruitmentPipelineDetailsRes findByPublicIdForTenant(String publicId) {
        RecruitmentPipeline plan = recruitmentPipelineRepository.findByPublicIdAndTenantId(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException("Evaluation plan not found: " + publicId));
        recruitmentPipelineAccessService.assertReadAccess(AuthService.getLoggedInUser(), plan);
        return RecruitmentPipelineDetailsRes.from(plan);
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<RecruitmentPipelineRes> getAll(AtsPageQuery query) {
        boolean isAdmin = recruitmentPipelineAccessService.isUserInRecruitmentPipelineAdminRole(AuthService.getLoggedInUser());
        Page<RecruitmentPipeline> page = isAdmin 
            ? recruitmentPipelineRepository.findAllForTenantAdmin(AuthService.getLoggedInTenantId(), query.toPageable()) 
            : recruitmentPipelineRepository.findAllForTenantNonAdmin(AuthService.getLoggedInTenantId(), AuthService.getLoggedInUser().getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(RecruitmentPipelineRes::from));
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<RecruitmentPipelineRes> getAllSystemTemplates(AtsPageQuery query) {
        Page<RecruitmentPipeline> page = recruitmentPipelineRepository.findByDeactivatedFalseAndIsPublicForAllTrue(query.toPageable());
        return AtsPageRsp.of(page.map(RecruitmentPipelineRes::from));
    }

    /**
     * Get all recruitment pipelines that are shared with current user or shared with all users
     * Only accessible to Recruitment Pipeline Admins (permission code: rpa).
     * @param query the pagination and status filter query
     * @return a paginated response containing recruitment pipelines for the tenant
     */
    @Transactional(readOnly = true)
    public AtsPageRsp<RecruitmentPipelineRes> getAllSharedWithUser(AtsPageQuery query) {
        Page<RecruitmentPipeline> page = recruitmentPipelineRepository.findPublicOrSharedForTenant(AuthService.getLoggedInTenantId(), query.toPageable());
        return AtsPageRsp.of(page.map(RecruitmentPipelineRes::from));
    }

    /**
     * Get all recruitment pipelines for the specified plan owner. 
     * Only accessible to Recruitment Pipeline Admins (permission code: rpa).
     * @param query the pagination and status filter query
     * @return a paginated response containing recruitment pipelines for the tenant
     */
    @Transactional(readOnly = true)
    public AtsPageRsp<RecruitmentPipelineRes> getAllForPlanOwner(AtsPageQuery query) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        Page<RecruitmentPipeline> page = recruitmentPipelineRepository.findByDeactivatedFalseAndTenantIdAndOwnerId(loggedInUser.getTenantId(), loggedInUser.getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(RecruitmentPipelineRes::from));
    }

    /**
     * Get all recruitment pipelines for logged in user's tenant. 
     * Only accessible to Recruitment Pipeline Admins (permission code: rpa).
     * @param query the pagination and status filter query
     * @return a paginated response containing recruitment pipelines for the tenant
     */
    private AtsPageRsp<RecruitmentPipelineRes> getAllForTenant(AtsPageQuery query) {
        Long tenantId = AuthService.getLoggedInTenantId();
        Page<RecruitmentPipeline> page = recruitmentPipelineRepository.findByDeactivatedFalseAndTenantId(tenantId, query.toPageable());
        return AtsPageRsp.of(page.map(RecruitmentPipelineRes::from));
    }

    private void setAnotherUserAsOwner(RecruitmentPipeline recruitmentPipeline, AccessType loggedInUserAccessType, String ownerId) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        if(loggedInUserAccessType != AccessType.TENANT_ADMIN && loggedInUserAccessType != AccessType.SYSTEM_ADMIN) {
            throw new InternalServerException("Attempt to set another user as owner without permission by " + loggedInUser.getPublicId());
        }

        User user = userService.findByPublicId(ownerId).orElseThrow(() -> new InternalServerException("Owner not found: " + ownerId));
        if(!user.getTenantId().equals(loggedInUser.getTenantId())) {
            throw new InternalServerException("Owner must belong to the same tenant as the logged in user. Owner: " + ownerId + ", Request by: " + loggedInUser.getPublicId());
        }
        recruitmentPipeline.setOwnerId(user.getId());
        recruitmentPipeline.setOwnerPublicId(user.getPublicId());
    }

    /**
     * Checks if the existing shared users are equal to the incoming set of user IDs.
     * @param existing the list of existing shared users
     * @param userIdsIncoming the set of incoming user IDs to compare against
     * @return true if the existing shared users match the incoming set of user IDs, false otherwise
     */
    private boolean sharedWithUsersEqual(List<RecruitmentPipelineSharedWithUser> existing, Set<String> userIdsIncoming) {
        List<RecruitmentPipelineSharedWithUser> a = existing == null ? List.of() : existing;
        if (a.size() != userIdsIncoming.size())
            return false;
        for (RecruitmentPipelineSharedWithUser planSharedWithUser : a) {
            if (!userIdsIncoming.contains(planSharedWithUser.getUserPublicId()))
                return false;
        }
        
        return true;
    }

    /**
     * Checks whether two lists of rounds are equal. Null lists are treated as empty.
     * The lists are equal when they have the same size and, comparing position by position,
     * every pair of rounds has the same order, round type, name, description and interviewers.
     * Name and description are compared with null and blank treated as the same value.
     * Interviewers are compared in sequence (same size, same ids in the same order).
     *
     * @param existing the rounds currently stored
     * @param incoming the rounds from the request
     * @return true if both lists are equivalent, false otherwise
     */
    private boolean roundsEqual(List<RecruitmentPipelineRound> existing, List<RecruitmentPipelineRound> incoming) {
        List<RecruitmentPipelineRound> a = existing == null ? List.of() : existing;
        List<RecruitmentPipelineRound> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            RecruitmentPipelineRound x = a.get(i);
            RecruitmentPipelineRound y = b.get(i);
            if (!Objects.equals(x.getOrder(), y.getOrder())
                    || !Objects.equals(x.getRoundType(), y.getRoundType())
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getName()), StringUtils.blankToNullAndTrim(y.getName()))
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getDescription()), StringUtils.blankToNullAndTrim(y.getDescription()))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks the rounds whose round type has isCustom=false (core stages):
     * 1. each core stage appears at most once, and
     * 2. the core stages keep the same relative order as their DB ids (ascending), when rounds are sorted by their order.
     * Custom stages can be placed anywhere and any number of times.
     * @param allRoundTypes
     * @param addedRounds
     * @param loggedInUserPublicId
     * @param planPublicId
     */
    private void assertRoundTypeOrder(List<RecruitmentPipelineRoundType> allRoundTypes, List<RecruitmentPipelineRound> addedRounds,
        String loggedInUserPublicId, String planPublicId
    ){
        Set<Integer> coreTypeIds = allRoundTypes.stream()
            .filter(type -> !Boolean.TRUE.equals(type.getIsCustom()))
            .map(RecruitmentPipelineRoundType::getId)
            .collect(Collectors.toSet());

        List<Integer> coreIdsInOrder = addedRounds.stream()
            .sorted(Comparator.comparing(RecruitmentPipelineRound::getOrder, Comparator.nullsLast(Comparator.naturalOrder())))
            .map(RecruitmentPipelineRound::getRoundType)
            .filter(coreTypeIds::contains)
            .toList();

        // strictly ascending ids means both "in id order" and "no duplicates"
        boolean orderValid = true;
        for (int i = 1; i < coreIdsInOrder.size() && orderValid; i++) {
            orderValid = coreIdsInOrder.get(i - 1) < coreIdsInOrder.get(i);
        }
        if(!orderValid){
            throw new InternalServerException("Invalid round type order. Plan: " + planPublicId + ", Request by: " + loggedInUserPublicId);
        }
    }

    private void assertUniqueWithinTenantForCreation(Long tenantId, String name) {
        if(recruitmentPipelineRepository.existsByDeactivatedFalseAndTenantIdAndName(tenantId, name)) {
            throw new InternalServerException("Duplicate evaluation plan within tenant: " + name);
        }
    }

    private void assertUniqueWithinTenantForUpdate(Long tenantId, String name, Long planId) {
        if(recruitmentPipelineRepository.existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(tenantId, name, planId)) {
            throw new InternalServerException("Duplicate evaluation plan within tenant: " + name);
        }
    }

    private void assertUniqueWithinAllForCreation(String name) {
        if(recruitmentPipelineRepository.existsByDeactivatedFalseAndName(name)) {
            throw new InternalServerException("Duplicate evaluation plan within all tenants: " + name);
        }
    }

    private void assertUniqueWithinAllForUpdate(String name, Long planId) {
        if(recruitmentPipelineRepository.existsByDeactivatedFalseAndNameAndIdNot(name, planId)) {
            throw new InternalServerException("Duplicate evaluation plan within all tenants: " + name);
        }
    }
}
