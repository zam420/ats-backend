package com.deepconnect.ats.rbac.service;

import com.deepconnect.ats.common.enums.RoleType;
import com.deepconnect.ats.common.exception.InternalServerException;
import com.deepconnect.ats.rbac.model.UserRole;
import com.deepconnect.ats.rbac.repo.UserRoleRepository;
import com.deepconnect.ats.rbac.model.Role;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final RoleService roleService;

    public boolean isUserInRole(Long userId, RoleType roleType) {
        return userRoleRepository.existsByUserIdAndRoleCode(userId, roleType.getCode());
    }

    public boolean addUserToRole(RoleType roleType, Long userId) {
        Role role = roleService.findByCode(roleType.getCode()).orElseThrow(() -> new InternalServerException("Role not found " + roleType.getCode()));
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getId());
        userRole.setRoleCode(role.getCode());
        userRoleRepository.saveAndFlush(userRole);
        return true;
    }

    public boolean removeUsersFromRole(Set<Long> userIds, RoleType roleType) {
        userRoleRepository.removeByUserIdInAndRoleCode(userIds, roleType.getCode());
        return true;
    }

    public Set<Long> getUserIdsInRoles(Set<Long> roleIds) {
        if (null == roleIds || roleIds.size() == 0) {
            return null;
        }
        return userRoleRepository.findByRoleIdIn(roleIds).stream().map(UserRole::getUserId)
                .collect(Collectors.toSet());
    }

    public Set<Long> getUserIdsByRole(Long roleId) {
        return userRoleRepository.findByRoleId(roleId).stream().map(UserRole::getUserId)
                .collect(Collectors.toSet());
    }

    public Set<UserRole> getUserRoles(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }
}
