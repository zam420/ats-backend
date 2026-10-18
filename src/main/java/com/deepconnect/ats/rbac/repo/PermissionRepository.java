package com.deepconnect.ats.rbac.repo;

import org.springframework.stereotype.Repository;

import com.deepconnect.ats.common.repo.BaseRepository;
import com.deepconnect.ats.rbac.model.Permission;

import java.util.Optional;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Long> {
    Optional<Permission> findByCode(String code);
}
