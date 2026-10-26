package com.deepconnect.ats.rbac.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.deepconnect.ats.common.repo.BaseRepository;
import com.deepconnect.ats.rbac.model.UserRole;

import java.util.Set;

@Repository
public interface UserRoleRepository extends BaseRepository<UserRole, Long> {
    UserRole findByUserIdAndRoleCode(Long userId, String roleCode);
    boolean existsByUserIdAndRoleCode(Long userId, String roleCode);

    Set<UserRole> findByUserId(Long userId);
    Set<UserRole> findByUserIdIn(Set<Long> userIds);
    Set<UserRole> findByRoleId(Long roleId);
    void deleteByUserId(Long userId);
    Set<UserRole> findByRoleIdIn(Set<Long> roleIds);
    
    @Modifying
    @Query(value = "delete from ats_user_role where user_id=?1 and role_id in ?2", nativeQuery = true)
    void removeByUserIdAndRoleIdIn(Long userId, Set<Long> roleIds);

    @Modifying
    @Query(value = "delete from ats_user_role where user_id in ?1 and role_code=?2", nativeQuery = true)
    void removeByUserIdInAndRoleCode(Set<Long> userIds, String roleCode);

}
