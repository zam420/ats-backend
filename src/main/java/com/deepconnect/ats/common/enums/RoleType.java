package com.deepconnect.ats.common.enums;

import java.util.Set;

import lombok.Getter;

@Getter
public enum RoleType {
    ADMIN(1L, "sa", "System Admin"),
    INTERVIEWER(6L, "itv", "Interviewer"),
    CANDIDATE(7L, "cdt", "Candidate"),
    ;

    private final Long id;
    private final String code;
    private final String name;

    RoleType(Long id, String code, String name) {
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public static boolean isInCertainRole(Set<Long> roleIds, RoleType targetRole) {
        for (Long roleId : roleIds) {
            if (roleId == targetRole.getId()) {
                return true;
            }
        }
        return false;
    }

    public static RoleType getByRoleId(Long roleId) {
        if (roleId == null) {
            return null;
        }
        for (RoleType roleType : values()) {
            if (roleType.getId() == roleId) {
                return roleType;
            }
        }
        return null;
    }
}
