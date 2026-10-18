package com.deepconnect.ats.rbac.repo;

import org.springframework.stereotype.Repository;

import com.deepconnect.ats.common.repo.BaseRepository;
import com.deepconnect.ats.rbac.model.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Long> {
    Optional<Role> findByCode(String code);
}
