package com.deepconnect.ats.tenant.service;

import org.springframework.stereotype.Service;

import com.deepconnect.ats.tenant.model.Tenant;
import com.deepconnect.ats.tenant.repo.TenantRepository;
import com.deepconnect.ats.auth.service.AuthService;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.tenant.dto.TenantReq;
import com.deepconnect.ats.tenant.dto.TenantRes;
import com.deepconnect.ats.tenant.enums.TenantType;
import com.deepconnect.ats.tenant.dto.TenantDetailsRes;
import com.deepconnect.ats.dto.AtsPageQuery;
import com.deepconnect.ats.dto.AtsPageRsp;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import org.springframework.data.domain.Page;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantAccessService tenantAccessService;

    private String save(Tenant plan, TenantReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        plan.setModifiedByUserId(loggedInUser.getUserId());
        plan.setModifiedByUserPublicId(loggedInUser.getPublicId());
        plan.setName(req.getName());
        plan.setDescription(req.getDescription());
        plan.setType(TenantType.CLIENT);
        return tenantRepository.save(plan).getPublicId();
    }

    @Transactional
    public String create(TenantReq req) {
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForCreation(req.getName());
        Tenant tenant = new Tenant();
        tenant.setCreatedByUserId(loggedInUser.getUserId());
        tenant.setCreatedByUserPublicId(loggedInUser.getPublicId());
        tenant.setOwnerId(loggedInUser.getUserId());
        tenant.setOwnerPublicId(loggedInUser.getPublicId());
        return this.save(tenant, req);
    }

    @Transactional
    public String update(TenantReq req) {        
        if (req.getId() == null) 
            throw new InternalServerException(String.format("Invalid Evaluation Plan Id: User: %s, Process: %s", AuthService.getLoggedInUser().getPublicId(), req.getId()));

        Tenant tenant = tenantRepository.findByPublicId(req.getId())
                .orElseThrow(() -> new InternalServerException(String.format("Evaluation Plan not found: User: %s, Plan: %s", AuthService.getLoggedInUser().getPublicId(), req.getId())));

        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        this.assertUniqueWithinAllForUpdate(req.getName(), tenant.getId());
        tenantAccessService.assertWriteAccess(loggedInUser, tenant);
        return this.save(tenant, req);
    }

    @Transactional
    public boolean deactivate(String publicId) {
        Tenant tenant = tenantRepository.findByPublicId(publicId).orElseThrow(() -> new InternalServerException(String.format("Tenant not found: User: %s, Tenant: %s", AuthService.getLoggedInUser().getPublicId(), publicId)));
        LoggedInUserDetails loggedInUser = AuthService.getLoggedInUser();
        tenantAccessService.assertWriteAccess(loggedInUser, tenant);
        tenant.setDeactivated(true);
        tenant.setModifiedByUserId(loggedInUser.getUserId());
        tenant.setModifiedByUserPublicId(loggedInUser.getPublicId());
        tenantRepository.save(tenant);
        return true;
    }

    @Transactional(readOnly = true)
    public TenantDetailsRes findByPublicId(String publicId) {
        Tenant tenant = tenantRepository.findByPublicId(publicId).orElseThrow(() -> new InternalServerException("Tenant not found: " + publicId));
        tenantAccessService.assertReadAccess(AuthService.getLoggedInUser(), tenant);
        return TenantDetailsRes.from(tenant);
    }

    @Transactional(readOnly = true)
    public AtsPageRsp<TenantRes> findAll(AtsPageQuery query) {
        boolean isAdmin = tenantAccessService.isUserInTenantSystemAdminRole(AuthService.getLoggedInUser());
        Page<Tenant> page = isAdmin 
            ? tenantRepository.findAllForTenantAdmin(query.toPageable()) 
            : tenantRepository.findAllForTenantNonAdmin(AuthService.getLoggedInUser().getUserId(), query.toPageable());
        return AtsPageRsp.of(page.map(TenantRes::from));
    }

    /**
     * Get all interview plans for the current tenant as a list. 
     * Used for selection dropdowns e.g. in job position forms
     * @return
     */
    @Transactional(readOnly = true)
    public List<TenantRes> findAllAsList() {
        boolean isAdmin = tenantAccessService.isUserInTenantSystemAdminRole(AuthService.getLoggedInUser());
        List<Tenant> tenants = isAdmin 
            ? tenantRepository.findAllForTenantAdminAsList() 
            : tenantRepository.findAllForTenantNonAdminAsList(AuthService.getLoggedInUser().getUserId());
        return tenants.stream().map(TenantRes::from).toList();
    }

    
    private void assertUniqueWithinAllForCreation(String name) {
        if(tenantRepository.existsByDeactivatedFalseAndName(name)) {
            throw new CustomException(CustomExceptionType.DUPLICATE_NAME, "tenant");
        }
    }

    private void assertUniqueWithinAllForUpdate(String name, Long tenantId) {
        if(tenantRepository.existsByDeactivatedFalseAndNameAndIdNot(name, tenantId)) {
            throw new CustomException(CustomExceptionType.DUPLICATE_NAME, "tenant");
        }
    }

}

