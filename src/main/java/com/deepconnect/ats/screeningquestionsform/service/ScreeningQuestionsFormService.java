package com.deepconnect.ats.screeningquestionsform.service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.user.service.UserService;
import com.deepconnect.ats.common.enums.AccessType;
import com.deepconnect.ats.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import com.deepconnect.ats.common.enums.ValidationFieldType;
import com.deepconnect.ats.common.exception.ValidationException;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionsFormRepository;
import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionRepository;
import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionTypeRepository;
import com.deepconnect.ats.screeningquestionsform.repo.ScreeningQuestionsFormShareRepository;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionType;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormRes;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormReq;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestion;
import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsFormShare;
import com.deepconnect.ats.screeningquestionsform.dto.ScreeningQuestionsFormDetailsRes;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreeningQuestionsFormService {

    @Value("${plans.maxRounds}")
    private int maxRounds;

    private final UserService userService;
    private final ScreeningQuestionsFormAccessService screeningQuestionTemplateAccessService;

    private final ScreeningQuestionsFormRepository screeningQuestionTemplateRepository;
    private final ScreeningQuestionRepository screeningQuestionRepository;
    private final ScreeningQuestionTypeRepository screeningQuestionTypeRepository;
    private final ScreeningQuestionsFormShareRepository screeningQuestionTemplateShareRepository;

    public List<ScreeningQuestionType> findAllScreeningQuestionTypes() {
        return screeningQuestionTypeRepository.findAll();
    }

    private String save(ScreeningQuestionsForm template, ScreeningQuestionsFormReq req, boolean questionsChanged, boolean sharedWithUsersChanged, boolean isPublicForTenant, boolean isPublicForAll) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        if(isPublicForAll && isPublicForTenant) {
            throw new InternalServerException(String.format("Cannot specify both isPublicForAll and isPublicForTenant. User: %s, Plan: %s", loggedInUser.getPublicId(), template.getPublicId()));
        }
        //Shared with users: if both isPublicForTenant and sharedWithUserIds are specified, throw internal server exception
        if(isPublicForTenant && !CollectionUtils.isEmpty(req.getSharedWithUserIds())) {
            throw new InternalServerException(String.format("Cannot specify both isPublicForTenant and sharedWithUserIds. User: %s, Plan: %s", loggedInUser.getPublicId(), template.getPublicId()));
        }
        if(CollectionUtils.isEmpty(req.getQuestions())){
            throw new ValidationException(CustomExceptionType.VALIDATION_MUST_ADD, ValidationFieldType.EVALUATION_PLAN_ROUND);
        }
        template.setModifiedByUserId(loggedInUser.getUserId());
        template.setModifiedByUserPublicId(loggedInUser.getPublicId());
        template.setName(req.getName());
        template.setDescription(req.getDescription());
        template.setIsPublicForAll(isPublicForAll);
        template.setIsPublicForTenant(isPublicForTenant);
        if(sharedWithUsersChanged){
            template.setIsSharedWithinTenant(!CollectionUtils.isEmpty(req.getSharedWithUserIds()));
        }
        template = screeningQuestionTemplateRepository.save(template);
        if (questionsChanged && !CollectionUtils.isEmpty(req.getQuestions())) {
            if (req.getQuestions().size() > maxRounds) {
                throw new InternalServerException(String.format("Too many questions. User: %s. Plan: %s. Questions: %d", loggedInUser.getPublicId(), template.getPublicId(), req.getQuestions().size()));
            }
            for (ScreeningQuestion question : req.getQuestions()) {
                question.setScreeningQuestionsForm(template);
                question.setScreeningQuestionsFormId(template.getId());
            }
            screeningQuestionRepository.saveAll(req.getQuestions());
        }
        
        if(sharedWithUsersChanged) {
            if(!CollectionUtils.isEmpty(template.getSharedWithUsers())) {
                screeningQuestionTemplateShareRepository.deleteByScreeningQuestionsFormId(template.getId());
            }
            if(!CollectionUtils.isEmpty(req.getSharedWithUserIds())){
                Set<User> sharedWithUsers = userService.getByPublicIds(req.getSharedWithUserIds());
                List<ScreeningQuestionsFormShare> sharedWithUserList = new ArrayList<>();
                for(User user : sharedWithUsers) {
                    ScreeningQuestionsFormShare sharedWithUser = new ScreeningQuestionsFormShare();
                    sharedWithUser.setScreeningQuestionsFormId(template.getId());
                    sharedWithUser.setScreeningQuestionsFormPublicId(template.getPublicId());
                    sharedWithUser.setUserId(user.getId());
                    sharedWithUser.setUserPublicId(user.getPublicId());
                    sharedWithUserList.add(sharedWithUser);
                }
                screeningQuestionTemplateShareRepository.saveAll(sharedWithUserList);
            }
        }
        
        return template.getPublicId();
    }

    @Transactional
    public String create(ScreeningQuestionsFormReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForCreation(loggedInUser.getTenantId(), req.getName());
        ScreeningQuestionsForm template = new ScreeningQuestionsForm();
        template.setCreatedByUserId(loggedInUser.getUserId());
        template.setCreatedByUserPublicId(loggedInUser.getPublicId());
        template.setTenantId(loggedInUser.getTenantId());
        if(req.getOwnerId() == null || req.getOwnerId().equals(loggedInUser.getPublicId())) {
            template.setOwnerId(loggedInUser.getUserId());
            template.setOwnerPublicId(loggedInUser.getPublicId());
        }
        else {
            setAnotherUserAsOwner(template, screeningQuestionTemplateAccessService.getAccessType(loggedInUser, template), req.getOwnerId());
        }

        return this.save(template, req, true, !CollectionUtils.isEmpty(req.getSharedWithUserIds()), req.getIsPublic() == null ? false : req.getIsPublic(), false);
    }

    @Transactional
    public String update(ScreeningQuestionsFormReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException(String.format("Invalid Screening Question Template Id: User: %s, Process: %s", AuthService.getLoggedInUser().getPublicId(), req.getId()));

        ScreeningQuestionsForm template = screeningQuestionTemplateRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException(String.format("Screening Question Template not found: User: %s, Plan: %s", AuthService.getLoggedInUser().getPublicId(), req.getId())));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinTenantForUpdate(loggedInUser.getTenantId(), req.getName(), template.getId());
        screeningQuestionTemplateAccessService.assertWriteAccess(loggedInUser, template);
        if(!loggedInUser.getPublicId().equals(req.getOwnerId())){//if the requested owner is not the logged in user, additional checks are needed
            setAnotherUserAsOwner(template, screeningQuestionTemplateAccessService.getAccessType(loggedInUser, template), req.getOwnerId());
        }

        boolean roundsChanged = !questionsEqual(template.getQuestions(), req.getQuestions());
        if (roundsChanged) {
            screeningQuestionRepository.deleteByScreeningQuestionsFormId(template.getId());
        }

        boolean isPublicForTenant = req.getIsPublic() == null ? false : req.getIsPublic();
        boolean sharedWithUsersChanged = false;
        //if previously plan was shared with specific users, but now it's shared with Everyone or None
        if(!CollectionUtils.isEmpty(template.getSharedWithUsers()) && (isPublicForTenant || CollectionUtils.isEmpty(req.getSharedWithUserIds()))) {
            sharedWithUsersChanged = true;
        }
        else if(!CollectionUtils.isEmpty(req.getSharedWithUserIds())) { //if the request has specific users to share with
            sharedWithUsersChanged = !sharedWithUsersEqual(template.getSharedWithUsers(), req.getSharedWithUserIds());
        }

        return this.save(template, req, roundsChanged, sharedWithUsersChanged, isPublicForTenant, false);
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
    public String createSystemTemplate(ScreeningQuestionsFormReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForCreation(req.getName());
        ScreeningQuestionsForm template = new ScreeningQuestionsForm();
        template.setCreatedByUserId(loggedInUser.getUserId());
        template.setCreatedByUserPublicId(loggedInUser.getPublicId());
        template.setTenantId(loggedInUser.getTenantId());
        template.setOwnerId(loggedInUser.getUserId());
        template.setOwnerPublicId(loggedInUser.getPublicId());
        return this.save(template, req, true, false, false, true);
    }

    /**
     * Updates an existing screening question template system template.
     * The difference between this and a regular update are:
     * 1. This method calls assertUniqueWithinAll while a regular update calls assertUniqueWithinTenant.
     * 2. This method doesn't allow changing the owner of the evaluation plan, unlike a regular update.
     * 3. This method always sets isPublicForTentant to false and isPublicForAll to true.
     * @param req
     * @return
     */
    @Transactional
    public String updateSystemTemplate(ScreeningQuestionsFormReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException("Invalid Evaluation Plan Id: " + req.getId());

        ScreeningQuestionsForm template = screeningQuestionTemplateRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException("Screening Question Template not found: " + req.getId()));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForUpdate(req.getName(), template.getId());
        screeningQuestionTemplateAccessService.assertWriteAccess(loggedInUser, template);
        boolean roundsChanged = !questionsEqual(template.getQuestions(), req.getQuestions());
        if (roundsChanged) {
            screeningQuestionRepository.deleteByScreeningQuestionsFormId(template.getId());
        }

        return this.save(template, req, roundsChanged, false, false, true);
    }

    @Transactional
    public boolean deactivate(String publicId) {
        ScreeningQuestionsForm plan = screeningQuestionTemplateRepository.findByPublicId(publicId).orElseThrow(() -> new InternalServerException(String.format("Screening Question Template not found: User: %s, Plan: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        screeningQuestionTemplateAccessService.assertWriteAccess(loggedInUser, plan);
        plan.setDeactivated(true);
        plan.setModifiedByUserId(loggedInUser.getUserId());
        plan.setModifiedByUserPublicId(loggedInUser.getPublicId());
        screeningQuestionTemplateRepository.save(plan);
        return true;
    }

    @Transactional(readOnly = true)
    public ScreeningQuestionsFormDetailsRes findByPublicIdForTenant(String publicId) {
        ScreeningQuestionsForm plan = screeningQuestionTemplateRepository.findByPublicIdAndTenantId(publicId, AuthService.getLoggedInTenantId()).orElseThrow(() -> new InternalServerException("Screening Question Template not found: " + publicId));
        screeningQuestionTemplateAccessService.assertReadAccess(AuthService.getLoggedInUser(), plan);
        return ScreeningQuestionsFormDetailsRes.from(plan);
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<ScreeningQuestionsFormRes> findAll(AtsPageQuery query) {
        boolean isAdmin = screeningQuestionTemplateAccessService.isUserInScreeningQuestionsFormAdminRole(AuthService.getLoggedInUser());
        Page<ScreeningQuestionsForm> page = isAdmin 
            ? screeningQuestionTemplateRepository.findAllForTenantAdmin(AuthService.getLoggedInTenantId(), query.toPageable()) 
            : screeningQuestionTemplateRepository.findAllForTenantNonAdmin(AuthService.getLoggedInTenantId(), AuthService.getLoggedInUser().getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(ScreeningQuestionsFormRes::from));
    }

    /**
     * Get all interview plans for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @return
     */
    @Transactional(readOnly = true)
    public List<ScreeningQuestionsFormRes> findAllAsList() {
        boolean isAdmin = screeningQuestionTemplateAccessService.isUserInScreeningQuestionsFormAdminRole(AuthService.getLoggedInUser());
        List<ScreeningQuestionsForm> plans = isAdmin 
            ? screeningQuestionTemplateRepository.findAllForTenantAdminAsList(AuthService.getLoggedInTenantId()) 
            : screeningQuestionTemplateRepository.findAllForTenantNonAdminAsList(AuthService.getLoggedInTenantId(), AuthService.getLoggedInUser().getUserId());
        return plans.stream().map(ScreeningQuestionsFormRes::from).toList();
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<ScreeningQuestionsFormRes> findAllSystemTemplates(AtsPageQuery query) {
        Page<ScreeningQuestionsForm> page = screeningQuestionTemplateRepository.findByDeactivatedFalseAndIsPublicForAllTrue(query.toPageable());
        return AtsPageRsp.of(page.map(ScreeningQuestionsFormRes::from));
    }

    /**
     * Get all interview plans that are shared with current user or shared with all users
     * Only accessible to Evaluation Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    @Transactional(readOnly = true)
    public AtsPageRsp<ScreeningQuestionsFormRes> findAllSharedWithUser(AtsPageQuery query) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        Page<ScreeningQuestionsForm> page = screeningQuestionTemplateRepository.findPublicOrSharedForTenant(loggedInUser.getTenantId(), loggedInUser.getUserId(),  query.toPageable());
        return AtsPageRsp.of(page.map(ScreeningQuestionsFormRes::from));
    }

    /**
     * Get all interview plans for the specified plan owner. 
     * Only accessible to Evaluation Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    @Transactional(readOnly = true)
    public AtsPageRsp<ScreeningQuestionsFormRes> findAllForPlanOwner(AtsPageQuery query) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        Page<ScreeningQuestionsForm> page = screeningQuestionTemplateRepository.findByDeactivatedFalseAndTenantIdAndOwnerId(loggedInUser.getTenantId(), loggedInUser.getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(ScreeningQuestionsFormRes::from));
    }

    /**
     * Get all interview plans for logged in user's tenant. 
     * Only accessible to Evaluation Plan Templates Admins (permission code: ipta).
     * @param query the pagination and status filter query
     * @return a paginated response containing interview plans for the tenant
     */
    private AtsPageRsp<ScreeningQuestionsFormRes> findAllForTenant(AtsPageQuery query) {
        Long tenantId = AuthService.getLoggedInTenantId();
        Page<ScreeningQuestionsForm> page = screeningQuestionTemplateRepository.findByDeactivatedFalseAndTenantId(tenantId, query.toPageable());
        return AtsPageRsp.of(page.map(ScreeningQuestionsFormRes::from));
    }

    private void setAnotherUserAsOwner(ScreeningQuestionsForm template, AccessType loggedInUserAccessType, String ownerId) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        if(loggedInUserAccessType != AccessType.TENANT_ADMIN && loggedInUserAccessType != AccessType.SYSTEM_ADMIN) {
            throw new InternalServerException("Attempt to set another user as owner without permission by " + loggedInUser.getPublicId());
        }

        User user = userService.findByPublicId(ownerId).orElseThrow(() -> new InternalServerException("Owner not found: " + ownerId));
        if(!user.getTenantId().equals(loggedInUser.getTenantId())) {
            throw new InternalServerException("Owner must belong to the same tenant as the logged in user. Owner: " + ownerId + ", Request by: " + loggedInUser.getPublicId());
        }
        template.setOwnerId(user.getId());
        template.setOwnerPublicId(user.getPublicId());
    }

    /**
     * Checks if the existing shared users are equal to the incoming set of user IDs.
     * @param existing the list of existing shared users
     * @param userIdsIncoming the set of incoming user IDs to compare against
     * @return true if the existing shared users match the incoming set of user IDs, false otherwise
     */
    private boolean sharedWithUsersEqual(List<ScreeningQuestionsFormShare> existing, Set<String> userIdsIncoming) {
        List<ScreeningQuestionsFormShare> a = existing == null ? List.of() : existing;
        if (a.size() != userIdsIncoming.size())
            return false;
        for (ScreeningQuestionsFormShare planSharedWithUser : a) {
            if (!userIdsIncoming.contains(planSharedWithUser.getUserPublicId()))
                return false;
        }
        
        return true;
    }

    /**
     * Checks whether two lists of screening questions are equal. Null lists are treated as empty.
     * The lists are equal when they have the same size and, comparing position by position,
     * every pair has the same order, question type, label, description, options, default option,
     * mandatory flag, multi-select flag and character limit. Label and description
     * are compared with null and blank treated as the same value. Options are compared in sequence.
     *
     * @param existing the screening questions currently stored
     * @param incoming the screening questions from the request
     * @return true if both lists are equivalent, false otherwise
     */
    private boolean questionsEqual(List<ScreeningQuestion> existing, List<ScreeningQuestion> incoming) {
        List<ScreeningQuestion> a = existing == null ? List.of() : existing;
        List<ScreeningQuestion> b = incoming == null ? List.of() : incoming;
        if (a.size() != b.size())
            return false;
        for (int i = 0; i < a.size(); i++) {
            ScreeningQuestion x = a.get(i);
            ScreeningQuestion y = b.get(i);
            if (!Objects.equals(x.getOrder(), y.getOrder())
                    || !Objects.equals(x.getScreeningQuestionTypeId(), y.getScreeningQuestionTypeId())
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getLabel()), StringUtils.blankToNullAndTrim(y.getLabel()))
                    || !Objects.equals(StringUtils.blankToNullAndTrim(x.getDescription()), StringUtils.blankToNullAndTrim(y.getDescription()))
                    || !Objects.equals(x.getOptions(), y.getOptions())
                    || !Objects.equals(x.getDefaultOption(), y.getDefaultOption())
                    || !Objects.equals(x.getIsMandatory(), y.getIsMandatory())
                    || !Objects.equals(x.getIsDropDownMultiSelect(), y.getIsDropDownMultiSelect())
                    || !Objects.equals(x.getMaxChars(), y.getMaxChars())) {
                return false;
            }
        }
        return true;
    }

    private void assertUniqueWithinTenantForCreation(Long tenantId, String name) {
        if(screeningQuestionTemplateRepository.existsByDeactivatedFalseAndTenantIdAndName(tenantId, name)) {
            throw new InternalServerException("Duplicate screening questions template within tenant: " + name);
        }
    }

    private void assertUniqueWithinTenantForUpdate(Long tenantId, String name, Long planId) {
        if(screeningQuestionTemplateRepository.existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(tenantId, name, planId)) {
            throw new InternalServerException("Duplicate screening questions template within tenant: " + name);
        }
    }

    private void assertUniqueWithinAllForCreation(String name) {
        if(screeningQuestionTemplateRepository.existsByDeactivatedFalseAndName(name)) {
            throw new InternalServerException("Duplicate screening questions template within all tenants: " + name);
        }
    }

    private void assertUniqueWithinAllForUpdate(String name, Long planId) {
        if(screeningQuestionTemplateRepository.existsByDeactivatedFalseAndNameAndIdNot(name, planId)) {
            throw new InternalServerException("Duplicate screening questions template within all tenants: " + name);
        }
    }

    public Optional<ScreeningQuestionsForm> findByPublicId(String publicId) {
        return screeningQuestionTemplateRepository.findByPublicId(publicId);
    }

}
