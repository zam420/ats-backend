package com.deepconnect.ats.rbac.service;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.deepconnect.ats.rbac.model.Role;
import com.deepconnect.ats.rbac.repo.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    public Optional<Role> findByCode(String roleCode) {
        return roleRepository.findByCode(roleCode);
    }

    public Set<Role> findAllById(Set<Long> roleIds) {
        return new HashSet<>(roleRepository.findAllById(roleIds));
    }
}

