package com.deepconnect.ats.job.repo;

import com.deepconnect.ats.job.model.JobPosition;
import com.deepconnect.ats.job.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.deepconnect.ats.common.repo.OperatableBaseRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobPositionRepository extends OperatableBaseRepository<JobPosition, Long>, JpaSpecificationExecutor<JobPosition> {
    //Optional<JobPosition> findByPublicId(String publicId);
    Optional<JobPosition> findByPublicIdAndTenantIdAndDeactivatedFalse(String publicId, Long tenantId);
    // List<JobPosition> findByStatus(JobStatus status);
    // Optional<JobPosition> findByTitle(String title);

    @Query("SELECT jp FROM JobPosition jp WHERE jp.tenantId = :tenantId")
    Page<JobPosition> findAllForTenantAdmin(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("SELECT jp FROM JobPosition jp WHERE jp.tenantId = :tenantId")
    List<JobPosition> findAllForTenantAdminAsList(@Param("tenantId") Long tenantId);

    @Query(
        value = "SELECT * FROM job_position jp WHERE jp.tenant_id = :tenantId AND (" +
                "jp.owner_id = :loggedInUserId " +
                "OR jp.recruiters LIKE CONCAT('%\"', :loggedInUserPublicId, '\"%') " +
                "OR jp.hiring_managers LIKE CONCAT('%\"', :loggedInUserPublicId, '\"%')" +
                ")",
        countQuery = "SELECT count(*) FROM job_position jp WHERE jp.tenant_id = :tenantId AND (" +
                    "jp.owner_id = :loggedInUserId " +
                    "OR jp.recruiters LIKE CONCAT('%\"', :loggedInUserPublicId, '\"%') " +
                    "OR jp.hiring_managers LIKE CONCAT('%\"', :loggedInUserPublicId, '\"%')" +
                    ")",
        nativeQuery = true
    )
    Page<JobPosition> findAllForTenantNonAdmin(@Param("tenantId") Long tenantId, @Param("loggedInUserId") Long loggedInUserId, @Param("loggedInUserPublicId") String loggedInUserPublicId, Pageable pageable);
    
    @Query(
        value = "SELECT * FROM job_position jp WHERE jp.tenant_id = :tenantId AND (" +
                "jp.owner_id = :loggedInUserId " +
                "OR jp.recruiters LIKE CONCAT('%\"', :loggedInUserPublicId, '\"%') " +
                "OR jp.hiring_managers LIKE CONCAT('%\"', :loggedInUserPublicId, '\"%')" +
                ")",
        countQuery = "SELECT count(*) FROM job_position jp WHERE jp.tenant_id = :tenantId AND (" +
                    "jp.owner_id = :loggedInUserId " +
                    "OR jp.recruiters LIKE CONCAT('%\"', :loggedInUserPublicId, '\"%') " +
                    "OR jp.hiring_managers LIKE CONCAT('%\"', :loggedInUserPublicId, '\"%')" +
                    ")",
        nativeQuery = true
    )
    List<JobPosition> findAllForTenantNonAdminAsList(@Param("tenantId") Long tenantId, @Param("loggedInUserId") Long loggedInUserId, @Param("loggedInUserPublicId") String loggedInUserPublicId);
    
}
