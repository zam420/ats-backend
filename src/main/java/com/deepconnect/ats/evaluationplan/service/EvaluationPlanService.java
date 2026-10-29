package com.deepconnect.ats.evaluationplan.service;
import org.springframework.transaction.annotation.Transactional;
import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanRoundTypeRepository;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanReq;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanRepository;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanRes;
import com.deepconnect.ats.evaluationplan.dto.EvaluationPlanDetailsRes;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRound;
import com.deepconnect.ats.evaluationplan.model.EvaluationPlanRoundType;
import com.deepconnect.ats.evaluationplan.repo.EvaluationPlanRoundRepository;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.user.service.UserService;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.common.utils.StringUtils;
import com.deepconnect.ats.common.utils.HashMapUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvaluationPlanService {

    private final UserService userService;
    private final EvaluationPlanAccessService evaluationPlanAccessService;

    private final EvaluationPlanRepository evaluationPlanRepository;
    private final EvaluationPlanRoundRepository evaluationPlanRoundRepository;
    private final EvaluationPlanRoundTypeRepository evaluationPlanRoundTypeRepository;

    public List<EvaluationPlanRoundType> getAllRoundTypes() {
        return evaluationPlanRoundTypeRepository.findAll();
    }

    private String save(EvaluationPlan plan, EvaluationPlanReq req, boolean saveRounds, boolean isPublicForTenant, boolean isPublicForAll) {
        plan.setModifiedByUserId(AuthService.getLoggedInUserId());
        plan.setModifiedByUserPublicId(AuthService.getLoggedInUser().getPublicId());
        plan.setName(req.getName());
        plan.setDescription(req.getDescription());
        plan.setIsPublicForTenant(isPublicForTenant);
        plan.setIsPublicForAll(isPublicForAll);
        plan = evaluationPlanRepository.save(plan);
        if (saveRounds && req.getRounds() != null) {
            HashMap<Integer, EvaluationPlanRoundType> roundTypesById = HashMapUtils.getMapFromList(this.getAllRoundTypes(), "Id", Integer.class, EvaluationPlanRoundType.class);
            for (EvaluationPlanRound roundTemplate : req.getRounds()) {
                roundTemplate.setEvaluationPlan(plan);
                roundTemplate.setEvaluationPlanId(plan.getId());
                if(!StringUtils.hasText(roundTemplate.getName())) {
                    EvaluationPlanRoundType roundType = roundTypesById.get(roundTemplate.getRoundType());
                    if(roundType != null)
                        roundTemplate.setName(roundType.getName());
                }
                // evaluationPlanRoundRepository.save(roundTemplate);
            }
            evaluationPlanRoundRepository.saveAll(req.getRounds());
        }

        return plan.getPublicId();
    }

    @Transactional
    public String create(EvaluationPlanReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForCreation(loggedInUser.getTenantId(), req.getName());
        EvaluationPlan plan = new EvaluationPlan();
        plan.setCreatedByUserId(loggedInUser.getUserId());
        plan.setCreatedByUserPublicId(loggedInUser.getPublicId());
        plan.setTenantId(loggedInUser.getTenantId());
        if(req.getOwnerId() == null) {
            plan.setOwnerId(loggedInUser.getUserId());
            plan.setOwnerPublicId(loggedInUser.getPublicId());
        }
        else {
            setAnotherUserAsOwner(plan, evaluationPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        }

        return this.save(plan, req, true, req.getIsPublic() == null ? false : req.getIsPublic(), false  );
    }

    @Transactional
    public String update(EvaluationPlanReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        EvaluationPlan plan = evaluationPlanRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForUpdate(loggedInUser.getTenantId(), req.getName(), plan.getId());
        evaluationPlanAccessService.assertWriteAccess(loggedInUser, plan);
        if(!loggedInUser.getPublicId().equals(req.getOwnerId())){//if the requested owner is not the logged in user, additional checks are needed
            setAnotherUserAsOwner(plan, evaluationPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        }

        boolean roundsChanged = !roundsEqual(plan.getRounds(), req.getRounds());
        if (roundsChanged) {
            evaluationPlanRoundRepository.deleteByEvaluationPlanId(plan.getId());
        }

        return this.save(plan, req, roundsChanged, req.getIsPublic() == null ? false : req.getIsPublic(), false);
    }

    /**
     * Creates a new evaluation plan system template.
     * The difference between this and a regular create are:
     * 1. This method calls assertUniqueWithinAll while a regular update calls assertUniqueWithinTenant.
     * 2. This method doesn't allow setting the owner of the evaluation plan, unlike a regular update. It automatically sets the owner to current user.
     * 3. This method always sets isPublicForTentant to false and isPublicForAll to true.
     * @param req
     * @return
     */
    @Transactional
    public String createTemplate(EvaluationPlanReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForCreation(req.getName());
        EvaluationPlan plan = new EvaluationPlan();
        plan.setCreatedByUserId(loggedInUser.getUserId());
        plan.setCreatedByUserPublicId(loggedInUser.getPublicId());
        plan.setTenantId(loggedInUser.getTenantId());
        plan.setOwnerId(loggedInUser.getUserId());
        plan.setOwnerPublicId(loggedInUser.getPublicId());
        return this.save(plan, req, true, false, true);
    }

    /**
     * Updates an existing evaluation plan system template.
     * The difference between this and a regular update are:
     * 1. This method calls assertUniqueWithinAll while a regular update calls assertUniqueWithinTenant.
     * 2. This method doesn't allow changing the owner of the evaluation plan, unlike a regular update.
     * 3. This method always sets isPublicForTentant to false and isPublicForAll to true.
     * @param req
     * @return
     */
    @Transactional
    public String updateTemplate(EvaluationPlanReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        EvaluationPlan plan = evaluationPlanRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForUpdate(req.getName(), plan.getId());
        evaluationPlanAccessService.assertWriteAccess(loggedInUser, plan);
        boolean roundsChanged = !roundsEqual(plan.getRounds(), req.getRounds());
        if (roundsChanged) {
            evaluationPlanRoundRepository.deleteByEvaluationPlanId(plan.getId());
        }

        return this.save(plan, req, roundsChanged, false, true);
    }

    @Transactional
    public boolean deactivate(String publicId) {
        EvaluationPlan plan = evaluationPlanRepository.findByPublicId(publicId).orElseThrow(() -> new InternalServerException("Interview Process not found: " + publicId));
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        evaluationPlanAccessService.assertWriteAccess(loggedInUser, plan);
        plan.setDeactivated(true);
        plan.setModifiedByUserId(loggedInUser.getUserId());
        plan.setModifiedByUserPublicId(loggedInUser.getPublicId());
        evaluationPlanRepository.save(plan);
        return true;
    }

    @Transactional(readOnly = true)
    public EvaluationPlanDetailsRes findByPublicIdForTenant(String publicId) {
        EvaluationPlan plan = evaluationPlanRepository.findByPublicIdAndTenantId(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException("Evaluation plan not found: " + publicId));
        evaluationPlanAccessService.assertReadAccess(AuthService.getLoggedInUser(), plan);
        return EvaluationPlanDetailsRes.from(plan);
    }

    public AtsPageRsp<EvaluationPlanRes> getAll(AtsPageQuery query) {
        boolean isAdmin = evaluationPlanAccessService.isUserInInterviewPlanAdminRole(AuthService.getLoggedInUser());
        Page<EvaluationPlan> page = isAdmin 
            ? evaluationPlanRepository.findAllForTenantAdmin(AuthService.getLoggedInTenantId(), query.toPageable()) 
            : evaluationPlanRepository.findAllForTenantNonAdmin(AuthService.getLoggedInTenantId(), AuthService.getLoggedInUser().getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(EvaluationPlanRes::from));
    }

    public AtsPageRsp<EvaluationPlanRes> getAllSystemTemplates(AtsPageQuery query) {
        Page<EvaluationPlan> page = evaluationPlanRepository.findByDeactivatedFalseAndIsPublicForAllTrue(query.toPageable());
        return AtsPageRsp.of(page.map(EvaluationPlanRes::from));
    }

    /**
     * Get all interview plans that are shared with current user or shared with all users
     * Only accessible to Evaluation Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    public AtsPageRsp<EvaluationPlanRes> getAllSharedWithUser(AtsPageQuery query) {
        Page<EvaluationPlan> page = evaluationPlanRepository.findByDeactivatedFalseAndIsPublicForTenantTrue(query.toPageable());
        return AtsPageRsp.of(page.map(EvaluationPlanRes::from));
    }

    /**
     * Get all interview plans for the specified plan owner. 
     * Only accessible to Evaluation Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    public AtsPageRsp<EvaluationPlanRes> getAllForPlanOwner(AtsPageQuery query) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        Page<EvaluationPlan> page = evaluationPlanRepository.findByDeactivatedFalseAndTenantIdAndOwnerId(loggedInUser.getTenantId(), loggedInUser.getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(EvaluationPlanRes::from));
    }

    /**
     * Get all interview plans for logged in user's tenant. 
     * Only accessible to Evaluation Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    private AtsPageRsp<EvaluationPlanRes> getAllForTenant(AtsPageQuery query) {
        Long tenantId = AuthService.getLoggedInTenantId();
        Page<EvaluationPlan> page = evaluationPlanRepository.findByDeactivatedFalseAndTenantId(tenantId, query.toPageable());
        return AtsPageRsp.of(page.map(EvaluationPlanRes::from));
    }

    private void setAnotherUserAsOwner(EvaluationPlan evaluationPlan, AccessType loggedInUserAccessType, String ownerId) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        if(loggedInUserAccessType != AccessType.TENANT_ADMIN && loggedInUserAccessType != AccessType.SYSTEM_ADMIN) {
            throw new InternalServerException("Attempt to set another user as owner without permission by " + loggedInUser.getPublicId());
        }

        User user = userService.findByPublicId(ownerId).orElseThrow(() -> new InternalServerException("Owner not found: " + ownerId));
        if(!user.getTenantId().equals(loggedInUser.getTenantId())) {
            throw new InternalServerException("Owner must belong to the same tenant as the logged in user. Owner: " + ownerId + ", Request by: " + loggedInUser.getPublicId());
        }
        evaluationPlan.setOwnerId(user.getId());
        evaluationPlan.setOwnerPublicId(user.getPublicId());
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
    private boolean roundsEqual(List<EvaluationPlanRound> existing, List<EvaluationPlanRound> incoming) {
        List<EvaluationPlanRound> a = existing == null ? List.of() : existing;
        List<EvaluationPlanRound> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            EvaluationPlanRound x = a.get(i);
            EvaluationPlanRound y = b.get(i);
            if (!Objects.equals(x.getOrder(), y.getOrder())
                    || !Objects.equals(x.getRoundType(), y.getRoundType())
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getName()), StringUtils.blankToNullAndTrim(y.getName()))
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getDescription()), StringUtils.blankToNullAndTrim(y.getDescription()))) {
                return false;
            }
            List<String> xi = x.getInterviewers() == null ? List.of() : x.getInterviewers();
            List<String> yi = y.getInterviewers() == null ? List.of() : y.getInterviewers();
            if (xi.size() != yi.size())
                return false;
            for (int j = 0; j < xi.size(); j++) {
                if (!Objects.equals(String.valueOf(xi.get(j)), String.valueOf(yi.get(j))))
                    return false;
            }
        }
        return true;
    }

    private void assertUniqueWithinTenantForCreation(Long tenantId, String name) {
        if(evaluationPlanRepository.existsByDeactivatedFalseAndTenantIdAndName(tenantId, name)) {
            throw new InternalServerException("Duplicate evaluation plan within tenant: " + name);
        }
    }

    private void assertUniqueWithinTenantForUpdate(Long tenantId, String name, Long planId) {
        if(evaluationPlanRepository.existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(tenantId, name, planId)) {
            throw new InternalServerException("Duplicate evaluation plan within tenant: " + name);
        }
    }

    private void assertUniqueWithinAllForCreation(String name) {
        if(evaluationPlanRepository.existsByDeactivatedFalseAndName(name)) {
            throw new InternalServerException("Duplicate evaluation plan within all tenants: " + name);
        }
    }

    private void assertUniqueWithinAllForUpdate(String name, Long planId) {
        if(evaluationPlanRepository.existsByDeactivatedFalseAndNameAndIdNot(name, planId)) {
            throw new InternalServerException("Duplicate evaluation plan within all tenants: " + name);
        }
    }
}
