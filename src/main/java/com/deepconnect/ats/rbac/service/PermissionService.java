package com.deepconnect.ats.rbac.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.deepconnect.ats.rbac.model.Permission;
import com.deepconnect.ats.rbac.repo.PermissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

    public Optional<Permission> findByCode(String permissionCode) {
        return permissionRepository.findByCode(permissionCode);
    }

    public Set<Permission> findAllById(Set<Long> permissionIds) {
        return new HashSet<>(permissionRepository.findAllById(permissionIds));
    }
}

