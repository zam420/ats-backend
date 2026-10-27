package com.deepconnect.ats.interviewplan.repo;

import com.deepconnect.ats.interviewplan.model.InterviewPlan;
import com.deepconnect.ats.common.repo.BaseWithTenantRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface InterviewPlanRepository extends BaseWithTenantRepository<InterviewPlan, Long> {

    Boolean existsByDeactivatedFalseAndName(String name);

    Boolean existsByDeactivatedFalseAndNameAndIdNot(String name, Long Id);

    Boolean existsByDeactivatedFalseAndTenantIdAndName(Long tenantId, String name);

    Boolean existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(Long tenantId, String name, Long Id);
    
    Page<InterviewPlan> findByDeactivatedFalse(Pageable pageable);

    Page<InterviewPlan> findByDeactivatedFalseAndTenantId(Long tenantId, Pageable pageable);

    Page<InterviewPlan> findByDeactivatedFalseAndTenantIdAndOwnerId(Long tenantId, Long ownerId, Pageable pageable);

    Page<InterviewPlan> findByDeactivatedFalseAndIsPublicForAllTrue(Pageable pageable);

    Page<InterviewPlan> findByDeactivatedFalseAndIsPublicForTenantTrue(Pageable pageable);

    @Query("SELECT ip FROM InterviewPlan ip WHERE ip.deactivated = false AND (ip.tenantId = :tenantId OR ip.isPublicForAll = true)")
    Page<InterviewPlan> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT ip FROM InterviewPlan ip WHERE ip.deactivated = false"
     + " AND ((ip.tenantId = :tenantId AND ip.ownerId = :ownerId) OR ((ip.tenantId = :tenantId OR ip.isPublicForTenant = true) OR ip.isPublicForAll = true))")
    Page<InterviewPlan> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId, Pageable pageable);
    

    // @Query("SELECT * FROM InterviewPlan WHERE deactivated = false AND (tenantId = :tenantId OR isPublicForAll = true)")
    // Page<InterviewPlan> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    // @Query("SELECT * FROM InterviewPlan WHERE deactivated = false"
    //  + " AND ((tenantId = :tenantId AND ownerId = :ownerId) OR ((tenantId = :tenantId OR isPublicForTenant = true) OR isPublicForAll = true)")
    // Page<InterviewPlan> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId, Pageable pageable);
    
}
