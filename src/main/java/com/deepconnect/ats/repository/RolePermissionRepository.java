package com.deepconnect.ats.repository;

import com.deepconnect.ats.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    Set<RolePermission> findByRoleId(Long roleId);
    Set<RolePermission> findByRoleIdIn(Set<Long> roleIds);
}
