package com.deepconnect.ats.rbac.repo;

import com.deepconnect.ats.rbac.model.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {
    Set<RolePermission> findByRoleId(Long roleId);
    Set<RolePermission> findByRoleIdIn(Set<Long> roleIds);
}
