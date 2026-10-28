package com.deepconnect.ats.interviewplan.service;
import org.springframework.transaction.annotation.Transactional;
import com.deepconnect.ats.interviewplan.repo.InterviewPlanRoundTypeRepository;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.interviewplan.dto.InterviewPlanReq;
import com.deepconnect.ats.interviewplan.model.InterviewPlan;
import com.deepconnect.ats.interviewplan.repo.InterviewPlanRepository;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.interviewplan.dto.InterviewPlanRes;
import com.deepconnect.ats.interviewplan.dto.InterviewPlanDetailsRes;
import com.deepconnect.ats.interviewplan.model.InterviewPlanRound;
import com.deepconnect.ats.interviewplan.model.InterviewPlanRoundType;
import com.deepconnect.ats.interviewplan.repo.InterviewPlanRoundRepository;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.user.service.UserService;
import com.deepconnect.ats.common.enums.AccessType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class InterviewPlanService {

    private final UserService userService;
    private final InterviewPlanAccessService interviewPlanAccessService;

    private final InterviewPlanRepository interviewPlanRepository;
    private final InterviewPlanRoundRepository interviewPlanRoundRepository;
    private final InterviewPlanRoundTypeRepository interviewPlanRoundTypeRepository;

    public List<InterviewPlanRoundType> getAllRoundTypes() {
        return interviewPlanRoundTypeRepository.findAll();
    }

    private String save(InterviewPlan plan, InterviewPlanReq req, boolean saveRounds, boolean isPublicForTenant, boolean isPublicForAll) {
        plan.setModifiedByUserId(AuthService.getLoggedInUserId());
        plan.setModifiedByUserPublicId(AuthService.getLoggedInUser().getPublicId());
        plan.setName(req.getName());
        plan.setDescription(req.getDescription());
        plan.setIsPublicForTenant(isPublicForTenant);
        plan.setIsPublicForAll(isPublicForAll);
        plan = interviewPlanRepository.save(plan);
        if (saveRounds && req.getRounds() != null) {
            for (InterviewPlanRound roundTemplate : req.getRounds()) {
                roundTemplate.setInterviewPlan(plan);
                roundTemplate.setInterviewPlanId(plan.getId());
                interviewPlanRoundRepository.save(roundTemplate);
            }
        }

        return plan.getPublicId();
    }

    @Transactional
    public String create(InterviewPlanReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForCreation(loggedInUser.getTenantId(), req.getName());
        InterviewPlan plan = new InterviewPlan();
        plan.setCreatedByUserId(loggedInUser.getUserId());
        plan.setCreatedByUserPublicId(loggedInUser.getPublicId());
        plan.setTenantId(loggedInUser.getTenantId());
        if(req.getOwnerId() == null) {
            plan.setOwnerId(loggedInUser.getUserId());
            plan.setOwnerPublicId(loggedInUser.getPublicId());
        }
        else {
            setAnotherUserAsOwner(plan, interviewPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        }

        return this.save(plan, req, true, req.getIsPublic() == null ? false : req.getIsPublic(), false  );
    }

    @Transactional
    public String update(InterviewPlanReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        InterviewPlan plan = interviewPlanRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForUpdate(loggedInUser.getTenantId(), req.getName(), plan.getId());
        interviewPlanAccessService.assertWriteAccess(loggedInUser, plan);
        if(!loggedInUser.getPublicId().equals(req.getOwnerId())){//if the requested owner is not the logged in user, additional checks are needed
            setAnotherUserAsOwner(plan, interviewPlanAccessService.getAccessType(loggedInUser, plan), req.getOwnerId());
        }

        boolean roundsChanged = !roundsEqual(plan.getRounds(), req.getRounds());
        if (roundsChanged) {
            interviewPlanRoundRepository.deleteByInterviewPlanId(plan.getId());
        }

        return this.save(plan, req, roundsChanged, req.getIsPublic() == null ? false : req.getIsPublic(), false);
    }

    /**
     * Creates a new interview plan system template.
     * The difference between this and a regular create are:
     * 1. This method calls assertUniqueWithinAll while a regular update calls assertUniqueWithinTenant.
     * 2. This method doesn't allow setting the owner of the interview plan, unlike a regular update. It automatically sets the owner to current user.
     * 3. This method always sets isPublicForTentant to false and isPublicForAll to true.
     * @param req
     * @return
     */
    @Transactional
    public String createTemplate(InterviewPlanReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForCreation(req.getName());
        InterviewPlan plan = new InterviewPlan();
        plan.setCreatedByUserId(loggedInUser.getUserId());
        plan.setCreatedByUserPublicId(loggedInUser.getPublicId());
        plan.setTenantId(loggedInUser.getTenantId());
        plan.setOwnerId(loggedInUser.getUserId());
        plan.setOwnerPublicId(loggedInUser.getPublicId());
        return this.save(plan, req, true, false, true);
    }

    /**
     * Updates an existing interview plan system template.
     * The difference between this and a regular update are:
     * 1. This method calls assertUniqueWithinAll while a regular update calls assertUniqueWithinTenant.
     * 2. This method doesn't allow changing the owner of the interview plan, unlike a regular update.
     * 3. This method always sets isPublicForTentant to false and isPublicForAll to true.
     * @param req
     * @return
     */
    @Transactional
    public String updateTemplate(InterviewPlanReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Interview Process Id: " + req.getId());

        InterviewPlan plan = interviewPlanRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Interview Process not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForUpdate(req.getName(), plan.getId());
        interviewPlanAccessService.assertWriteAccess(loggedInUser, plan);
        boolean roundsChanged = !roundsEqual(plan.getRounds(), req.getRounds());
        if (roundsChanged) {
            interviewPlanRoundRepository.deleteByInterviewPlanId(plan.getId());
        }

        return this.save(plan, req, roundsChanged, false, true);
    }

    @Transactional
    public boolean deactivate(String publicId) {
        InterviewPlan plan = interviewPlanRepository.findByPublicId(publicId).orElseThrow(() -> new InternalServerException("Interview Process not found: " + publicId));
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        interviewPlanAccessService.assertWriteAccess(loggedInUser, plan);
        plan.setDeactivated(true);
        plan.setModifiedByUserId(loggedInUser.getUserId());
        plan.setModifiedByUserPublicId(loggedInUser.getPublicId());
        interviewPlanRepository.save(plan);
        return true;
    }

    @Transactional(readOnly = true)
    public InterviewPlanDetailsRes findByPublicIdForTenant(String publicId) {
        InterviewPlan plan = interviewPlanRepository.findByPublicIdAndTenantId(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException("Interview plan not found: " + publicId));
        interviewPlanAccessService.assertReadAccess(AuthService.getLoggedInUser(), plan);
        return InterviewPlanDetailsRes.from(plan);
    }

    public AtsPageRsp<InterviewPlanRes> getAll(AtsPageQuery query) {
        boolean isAdmin = interviewPlanAccessService.isUserInInterviewPlanAdminRole(AuthService.getLoggedInUser());
        Page<InterviewPlan> page = isAdmin 
            ? interviewPlanRepository.findAllForTenantAdmin(AuthService.getLoggedInTenantId(), query.toPageable()) 
            : interviewPlanRepository.findAllForTenantNonAdmin(AuthService.getLoggedInTenantId(), AuthService.getLoggedInUser().getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(InterviewPlanRes::from));
    }

    public AtsPageRsp<InterviewPlanRes> getAllSystemTemplates(AtsPageQuery query) {
        Page<InterviewPlan> page = interviewPlanRepository.findByDeactivatedFalseAndIsPublicForAllTrue(query.toPageable());
        return AtsPageRsp.of(page.map(InterviewPlanRes::from));
    }

    /**
     * Get all interview plans that are shared with current user or shared with all users
     * Only accessible to Interview Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    public AtsPageRsp<InterviewPlanRes> getAllSharedWithUser(AtsPageQuery query) {
        Page<InterviewPlan> page = interviewPlanRepository.findByDeactivatedFalseAndIsPublicForTenantTrue(query.toPageable());
        return AtsPageRsp.of(page.map(InterviewPlanRes::from));
    }

    /**
     * Get all interview plans for the specified plan owner. 
     * Only accessible to Interview Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    public AtsPageRsp<InterviewPlanRes> getAllForPlanOwner(AtsPageQuery query) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        Page<InterviewPlan> page = interviewPlanRepository.findByDeactivatedFalseAndTenantIdAndOwnerId(loggedInUser.getTenantId(), loggedInUser.getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(InterviewPlanRes::from));
    }

    /**
     * Get all interview plans for logged in user's tenant. 
     * Only accessible to Interview Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    private AtsPageRsp<InterviewPlanRes> getAllForTenant(AtsPageQuery query) {
        Long tenantId = AuthService.getLoggedInTenantId();
        Page<InterviewPlan> page = interviewPlanRepository.findByDeactivatedFalseAndTenantId(tenantId, query.toPageable());
        return AtsPageRsp.of(page.map(InterviewPlanRes::from));
    }

    private void setAnotherUserAsOwner(InterviewPlan interviewPlan, AccessType loggedInUserAccessType, String ownerId) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        if(loggedInUserAccessType != AccessType.TENANT_ADMIN && loggedInUserAccessType != AccessType.SYSTEM_ADMIN) {
            throw new InternalServerException("Attempt to set another user as owner without permission by " + loggedInUser.getPublicId());
        }

        User user = userService.findByPublicId(ownerId).orElseThrow(() -> new InternalServerException("Owner not found: " + ownerId));
        if(!user.getTenantId().equals(loggedInUser.getTenantId())) {
            throw new InternalServerException("Owner must belong to the same tenant as the logged in user. Owner: " + ownerId + ", Request by: " + loggedInUser.getPublicId());
        }
        interviewPlan.setOwnerId(user.getId());
        interviewPlan.setOwnerPublicId(user.getPublicId());
    }

    private boolean roundsEqual(List<InterviewPlanRound> existing, List<InterviewPlanRound> incoming) {
        List<InterviewPlanRound> a = existing == null ? List.of() : existing;
        List<InterviewPlanRound> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            InterviewPlanRound x = a.get(i);
            InterviewPlanRound y = b.get(i);
            if (!Objects.equals(x.getOrder(), y.getOrder())
                    || !Objects.equals(x.getRoundType(), y.getRoundType())) {
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
        if(interviewPlanRepository.existsByDeactivatedFalseAndTenantIdAndName(tenantId, name)) {
            throw new InternalServerException("Duplicate interview plan within tenant: " + name);
        }
    }

    private void assertUniqueWithinTenantForUpdate(Long tenantId, String name, Long planId) {
        if(interviewPlanRepository.existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(tenantId, name, planId)) {
            throw new InternalServerException("Duplicate interview plan within tenant: " + name);
        }
    }

    private void assertUniqueWithinAllForCreation(String name) {
        if(interviewPlanRepository.existsByDeactivatedFalseAndName(name)) {
            throw new InternalServerException("Duplicate interview plan within all tenants: " + name);
        }
    }

    private void assertUniqueWithinAllForUpdate(String name, Long planId) {
        if(interviewPlanRepository.existsByDeactivatedFalseAndNameAndIdNot(name, planId)) {
            throw new InternalServerException("Duplicate interview plan within all tenants: " + name);
        }
    }
}
