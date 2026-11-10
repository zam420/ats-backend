package com.deepconnect.ats.evaluationplan.repo;

import com.deepconnect.ats.evaluationplan.model.EvaluationPlan;
import com.deepconnect.ats.common.repo.BaseWithTenantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EvaluationPlanRepository extends BaseWithTenantRepository<EvaluationPlan, Long> {

    Boolean existsByDeactivatedFalseAndName(String name);

    Boolean existsByDeactivatedFalseAndNameAndIdNot(String name, Long Id);

    Boolean existsByDeactivatedFalseAndTenantIdAndName(Long tenantId, String name);

    Boolean existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(Long tenantId, String name, Long Id);
    
    Page<EvaluationPlan> findByDeactivatedFalse(Pageable pageable);

    Page<EvaluationPlan> findByDeactivatedFalseAndTenantId(Long tenantId, Pageable pageable);

    Page<EvaluationPlan> findByDeactivatedFalseAndTenantIdAndOwnerId(Long tenantId, Long ownerId, Pageable pageable);

    Page<EvaluationPlan> findByDeactivatedFalseAndIsPublicForAllTrue(Pageable pageable);

    // Page<EvaluationPlan> findByDeactivatedFalseAndIsPublicForTenantTrueAndTenantId(Long tenantId, Pageable pageable);

    // Tenant-public plans, plus the tenant's plans that are shared with at least one specific user
    @Query("SELECT ip FROM EvaluationPlan ip WHERE ip.deactivated = false AND ip.tenantId = :tenantId"
     + " AND (ip.isPublicForTenant = true"
     + " OR EXISTS (SELECT 1 FROM EvaluationPlanShare s WHERE s.evaluationPlanId = ip.id))")
    Page<EvaluationPlan> findPublicOrSharedForTenant(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT ip FROM EvaluationPlan ip WHERE ip.deactivated = false AND (ip.tenantId = :tenantId OR ip.isPublicForAll = true)")
    Page<EvaluationPlan> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT ip FROM EvaluationPlan ip WHERE ip.deactivated = false AND (ip.tenantId = :tenantId OR ip.isPublicForAll = true)")
    List<EvaluationPlan> findAllForTenantAdminAsList(@Param("tenantId") Long tenantId);

    @Query("SELECT ip FROM EvaluationPlan ip WHERE ip.deactivated = false"
     + " AND ((ip.tenantId = :tenantId AND ip.ownerId = :loggedInUserId)"                                  //owner within tenant
     + " OR (ip.tenantId = :tenantId AND ip.isPublicForTenant = true)"                              //tenant-public plans
     + " OR EXISTS (SELECT 1 FROM EvaluationPlanShare s WHERE s.evaluationPlanId = ip.id)" //shared with user
     + " OR ip.isPublicForAll = true)")                                                             //system templates
    Page<EvaluationPlan> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("loggedInUserId") Long loggedInUserId, Pageable pageable);
    
    @Query("SELECT ip FROM EvaluationPlan ip WHERE ip.deactivated = false"
     + " AND ((ip.tenantId = :tenantId AND ip.ownerId = :loggedInUserId)"                                  //owner within tenant
     + " OR (ip.tenantId = :tenantId AND ip.isPublicForTenant = true)"                              //tenant-public plans
     + " OR EXISTS (SELECT 1 FROM EvaluationPlanShare s WHERE s.evaluationPlanId = ip.id)" //shared with user
     + " OR ip.isPublicForAll = true)")                                                             //system templates
    List<EvaluationPlan> findAllForTenantNonAdminAsList(@Param("tenantId") Long tenantId, @Param("loggedInUserId") Long loggedInUserId);
    

    // @Query("SELECT * FROM EvaluationPlan WHERE deactivated = false AND (tenantId = :tenantId OR isPublicForAll = true)")
    // Page<EvaluationPlan> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    // @Query("SELECT * FROM EvaluationPlan WHERE deactivated = false"
    //  + " AND ((tenantId = :tenantId AND ownerId = :ownerId) OR ((tenantId = :tenantId OR isPublicForTenant = true) OR isPublicForAll = true)")
    // Page<EvaluationPlan> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId, Pageable pageable);
    
}
