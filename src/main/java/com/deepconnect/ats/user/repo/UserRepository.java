package com.deepconnect.ats.user.repo;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.common.repo.BaseRepository;

@Repository
public interface UserRepository extends BaseRepository<User, Long>, JpaSpecificationExecutor<User> {
    List<User> findByType(Integer type);
    List<User> findByStatus(Integer status);
    List<User> findByIdIn(Set<Long> ids);

    @Modifying
    @Query(value = "delete from ats_user where email=?1",nativeQuery = true)
    void deleteUser(String email);

    // ZAM: all methods below this are new and safe. Above this line are old and need review.

    Optional<User> findByEmail(String email);

    List<User> findByTenantIdAndDeactivatedFalseAndLockedFalse(Long tenantId);

    @Query("SELECT ur.roleCode FROM UserRole ur WHERE ur.userId = :userId")
    List<String> findRoleCodesByUserId(@Param("userId") Long userId);

    @Query("SELECT ur.roleCode FROM UserRole ur WHERE ur.userPublicId = :userPublicId")
    List<String> findRoleCodesByUserPublicId(@Param("userPublicId") String userPublicId);

    @Query("SELECT DISTINCT u FROM User u JOIN UserRole ur ON u.id = ur.userId " +
           "WHERE u.tenantId = :tenantId " +
           "AND u.deactivated = false " +
           "AND u.locked = false " +
           "AND ur.roleCode IN :roleCodes")
    List<User> findActiveUsersByTenantAndRoleCodes(
        @Param("tenantId") Long tenantId, 
        @Param("roleCodes") Set<String> roleCodes
    );
    
}
