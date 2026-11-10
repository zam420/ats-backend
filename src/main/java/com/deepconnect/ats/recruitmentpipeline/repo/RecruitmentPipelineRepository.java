package com.deepconnect.ats.recruitmentpipeline.repo;

import com.deepconnect.ats.recruitmentpipeline.model.RecruitmentPipeline;
import com.deepconnect.ats.common.repo.BaseWithTenantRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

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

    // Page<RecruitmentPipeline> findByDeactivatedFalseAndIsPublicForTenantTrue(Pageable pageable);

    // Tenant-public plans, plus the tenant's plans that are shared with at least one specific user
    @Query("SELECT ip FROM RecruitmentPipeline ip WHERE ip.deactivated = false AND ip.tenantId = :tenantId"
     + " AND (ip.isPublicForTenant = true"
     + " OR EXISTS (SELECT 1 FROM RecruitmentPipelineShare s WHERE s.recruitmentPipelineId = ip.id))")
    Page<RecruitmentPipeline> findPublicOrSharedForTenant(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT ip FROM RecruitmentPipeline ip WHERE ip.deactivated = false AND (ip.tenantId = :tenantId OR ip.isPublicForAll = true)")
    Page<RecruitmentPipeline> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT ip FROM RecruitmentPipeline ip WHERE ip.deactivated = false AND (ip.tenantId = :tenantId OR ip.isPublicForAll = true)")
    List<RecruitmentPipeline> findAllForTenantAdminAsList(@Param("tenantId") Long tenantId);

    @Query("SELECT ip FROM RecruitmentPipeline ip WHERE ip.deactivated = false"
     + " AND ((ip.tenantId = :tenantId AND ip.ownerId = :loggedInUserId)"                                              //owner within tenant
     + " OR (ip.tenantId = :tenantId AND ip.isPublicForTenant = true)"                                          //tenant-public plans
     + " OR EXISTS (SELECT 1 FROM RecruitmentPipelineShare s WHERE s.recruitmentPipelineId = ip.id)"   //shared with user
     + " OR ip.isPublicForAll = true)")                                                                         //system templates
    Page<RecruitmentPipeline> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("loggedInUserId") Long loggedInUserId, Pageable pageable);

    @Query("SELECT ip FROM RecruitmentPipeline ip WHERE ip.deactivated = false"
     + " AND ((ip.tenantId = :tenantId AND ip.ownerId = :loggedInUserId)"                                  //owner within tenant
     + " OR (ip.tenantId = :tenantId AND ip.isPublicForTenant = true)"                              //tenant-public plans
     + " OR EXISTS (SELECT 1 FROM RecruitmentPipelineShare s WHERE s.recruitmentPipelineId = ip.id)" //shared with user
     + " OR ip.isPublicForAll = true)")                                                             //system templates
    List<RecruitmentPipeline> findAllForTenantNonAdminAsList(@Param("tenantId") Long tenantId, @Param("loggedInUserId") Long loggedInUserId);
    

    // @Query("SELECT * FROM RecruitmentPipeline WHERE deactivated = false AND (tenantId = :tenantId OR isPublicForAll = true)")
    // Page<RecruitmentPipeline> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    // @Query("SELECT * FROM RecruitmentPipeline WHERE deactivated = false"
    //  + " AND ((tenantId = :tenantId AND ownerId = :ownerId) OR ((tenantId = :tenantId OR isPublicForTenant = true) OR isPublicForAll = true)")
    // Page<RecruitmentPipeline> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("ownerId") Long ownerId, Pageable pageable);
    
}
