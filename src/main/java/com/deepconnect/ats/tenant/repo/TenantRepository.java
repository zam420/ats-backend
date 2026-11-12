package com.deepconnect.ats.tenant.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.deepconnect.ats.common.repo.OperatableBaseRepository;
import com.deepconnect.ats.tenant.model.Tenant;
import com.deepconnect.ats.tenant.enums.TenantType;

import java.util.List;

@Repository
public interface TenantRepository extends OperatableBaseRepository<Tenant, Long> {

    Boolean existsByDeactivatedFalseAndName(String name);

    Boolean existsByDeactivatedFalseAndNameAndIdNot(String name, Long Id);

    java.util.Optional<Tenant> findByDeactivatedFalseAndPublicIdAndType(String publicId, TenantType type);

    @Query("SELECT ip FROM Tenant ip WHERE ip.deactivated = false AND ip.type = :type")
    Page<Tenant> findAllForTenantAdmin(@Param("type") TenantType type, Pageable pageable);

    @Query("SELECT ip FROM Tenant ip WHERE ip.deactivated = false AND ip.type = :type")
    List<Tenant> findAllForTenantAdminAsList(@Param("type") TenantType type);

    @Query("SELECT ip FROM Tenant ip WHERE ip.deactivated = false AND  ip.type = :type AND ip.ownerId = :loggedInUserId")                                                             
    Page<Tenant> findAllForTenantNonAdmin(@Param("type") TenantType type, @Param("loggedInUserId") Long loggedInUserId, Pageable pageable);
    
    @Query("SELECT ip FROM Tenant ip WHERE ip.deactivated = false AND  ip.type = :type AND ip.ownerId = :loggedInUserId")
    List<Tenant> findAllForTenantNonAdminAsList(@Param("type") TenantType type, @Param("loggedInUserId") Long loggedInUserId);
   
}
