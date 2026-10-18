package com.deepconnect.ats.rbac.service;

import com.deepconnect.ats.common.enums.RoleType;
import com.deepconnect.ats.rbac.model.UserRole;
import com.deepconnect.ats.rbac.repo.UserRoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRoleConfigService {

    private final UserRoleRepository userRoleRepository;

    public boolean inRole(Long userId, RoleType roleType) {
        Set<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        if (CollectionUtils.isEmpty(userRoles)) {
            return false;
        }
        Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return RoleType.isInCertainRole(roleIds, roleType);
    }

    public boolean addUserToRole(RoleType roleType, Long userId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(roleType.getId());
        userRole.setRoleCode(roleType.getCode());
        userRoleRepository.saveAndFlush(userRole);
        return true;
    }

    public boolean removeUsersFromRole(Set<Long> userIds, RoleType roleType) {
        userRoleRepository.removeByUserIdInAndRoleId(userIds, roleType.getId());
        return true;
    }


    public boolean inAdminUserRole(Long userId) {
        if (null == userId) {
            return false;
        }
        Set<UserRole> userRoles = userRoleRepository.findByUserId(userId);
        if (CollectionUtils.isEmpty(userRoles)) {
            return false;
        }
        Set<Long> roleIds = userRoles.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        return RoleType.isInCertainRole(roleIds, RoleType.ADMIN);
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
