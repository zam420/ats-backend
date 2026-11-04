package com.deepconnect.ats.recruitmentpipeline.repo;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.common.repo.BaseWithTenantRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RecruitmentPipelineRepository extends BaseWithTenantRepository<RecruitmentPipeline, Long> {

    Boolean existsByDeactivatedFalseAndName(String name);

    Boolean existsByDeactivatedFalseAndNameAndIdNot(String name, Long Id);

    Boolean existsByDeactivatedFalseAndTenantIdAndName(Long tenantId, String name);

    Boolean existsByDeactivatedFalseAndTenantIdAndNameAndIdNot(Long tenantId, String name, Long Id);
    
    Page<RecruitmentPipeline> findByDeactivatedFalse(Pageable pageable);

    Page<RecruitmentPipeline> findByDeactivatedFalseAndTenantId(Long tenantId, Pageable pageable);

    Page<RecruitmentPipeline> findByDeactivatedFalseAndTenantIdAndOwnerId(Long tenantId, Long ownerId, Pageable pageable);

    Page<RecruitmentPipeline> findByDeactivatedFalseAndIsPublicForAllTrue(Pageable pageable);

    Page<RecruitmentPipeline> findByDeactivatedFalseAndIsPublicForTenantTrue(Pageable pageable);

    @Query("SELECT ip FROM RecruitmentPipeline ip WHERE ip.deactivated = false AND (ip.tenantId = :tenantId OR ip.isPublicForAll = true)")
    Page<RecruitmentPipeline> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT ip FROM RecruitmentPipeline ip WHERE ip.deactivated = false"
     + " AND ((ip.tenantId = :tenantId AND ip.ownerId = :ownerId) OR ((ip.tenantId = :tenantId OR ip.isPublicForTenant = true) OR ip.isPublicForAll = true))")
    Page<RecruitmentPipeline> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId, Pageable pageable);
    

    // @Query("SELECT * FROM RecruitmentPipeline WHERE deactivated = false AND (tenantId = :tenantId OR isPublicForAll = true)")
    // Page<RecruitmentPipeline> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    // @Query("SELECT * FROM RecruitmentPipeline WHERE deactivated = false"
    //  + " AND ((tenantId = :tenantId AND ownerId = :ownerId) OR ((tenantId = :tenantId OR isPublicForTenant = true) OR isPublicForAll = true)")
    // Page<RecruitmentPipeline> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId, Pageable pageable);
    
}
