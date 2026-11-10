package com.deepconnect.ats.screeningquestionsform.repo;

import com.deepconnect.ats.screeningquestionsform.model.ScreeningQuestionsForm;
import com.deepconnect.ats.common.repo.BaseWithTenantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ScreeningQuestionsFormRepository extends BaseWithTenantRepository<ScreeningQuestionsForm, Long> {

    Boolean existsByDeactivatedFalseAndName(String name);

    Boolean existsByDeactivatedFalseAndNameAndIdNot(String name, Long Id);

    Boolean existsByDeactivatedFalseAndTenantIdAndName(Long tenantId, String name);

    Boolean existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(Long tenantId, String name, Long Id);
    
    Page<ScreeningQuestionsForm> findByDeactivatedFalse(Pageable pageable);

    Page<ScreeningQuestionsForm> findByDeactivatedFalseAndTenantId(Long tenantId, Pageable pageable);

    Page<ScreeningQuestionsForm> findByDeactivatedFalseAndTenantIdAndOwnerId(Long tenantId, Long ownerId, Pageable pageable);

    Page<ScreeningQuestionsForm> findByDeactivatedFalseAndIsPublicForAllTrue(Pageable pageable);

    // Page<ScreeningQuestionsForm> findByDeactivatedFalseAndIsPublicForTenantTrueAndTenantId(Long tenantId, Pageable pageable);

    // Tenant-public plans, plus the tenant's plans that are shared with at least one specific user
    @Query("SELECT ip FROM ScreeningQuestionsForm ip WHERE ip.deactivated = false AND ip.tenantId = :tenantId"
     + " AND (ip.isPublicForTenant = true"
     + " OR EXISTS (SELECT 1 FROM ScreeningQuestionsFormShare s WHERE s.screeningQuestionsFormId = ip.id))")
    Page<ScreeningQuestionsForm> findPublicOrSharedForTenant(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT ip FROM ScreeningQuestionsForm ip WHERE ip.deactivated = false AND (ip.tenantId = :tenantId OR ip.isPublicForAll = true)")
    Page<ScreeningQuestionsForm> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT ip FROM ScreeningQuestionsForm ip WHERE ip.deactivated = false AND (ip.tenantId = :tenantId OR ip.isPublicForAll = true)")
    List<ScreeningQuestionsForm> findAllForTenantAdminAsList(@Param("tenantId") Long tenantId);

    @Query("SELECT ip FROM ScreeningQuestionsForm ip WHERE ip.deactivated = false"
     + " AND ((ip.tenantId = :tenantId AND ip.ownerId = :loggedInUserId)"                                  //owner within tenant
     + " OR (ip.tenantId = :tenantId AND ip.isPublicForTenant = true)"                              //tenant-public plans
     + " OR EXISTS (SELECT 1 FROM ScreeningQuestionsFormShare s WHERE s.screeningQuestionsFormId = ip.id)" //shared with user
     + " OR ip.isPublicForAll = true)")                                                             //system templates
    Page<ScreeningQuestionsForm> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("loggedInUserId") Long loggedInUserId, Pageable pageable);
    
    @Query("SELECT ip FROM ScreeningQuestionsForm ip WHERE ip.deactivated = false"
     + " AND ((ip.tenantId = :tenantId AND ip.ownerId = :loggedInUserId)"                                  //owner within tenant
     + " OR (ip.tenantId = :tenantId AND ip.isPublicForTenant = true)"                              //tenant-public plans
     + " OR EXISTS (SELECT 1 FROM ScreeningQuestionsFormShare s WHERE s.screeningQuestionsFormId = ip.id)" //shared with user
     + " OR ip.isPublicForAll = true)")                                                             //system templates
    List<ScreeningQuestionsForm> findAllForTenantNonAdminAsList(@Param("tenantId") Long tenantId, @Param("loggedInUserId") Long loggedInUserId);
    

    // @Query("SELECT * FROM ScreeningQuestionsForm WHERE deactivated = false AND (tenantId = :tenantId OR isPublicForAll = true)")
    // Page<ScreeningQuestionsForm> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    // @Query("SELECT * FROM ScreeningQuestionsForm WHERE deactivated = false"
    //  + " AND ((tenantId = :tenantId AND ownerId = :ownerId) OR ((tenantId = :tenantId OR isPublicForTenant = true) OR isPublicForAll = true)")
    // Page<ScreeningQuestionsForm> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId, Pageable pageable);
    
}
