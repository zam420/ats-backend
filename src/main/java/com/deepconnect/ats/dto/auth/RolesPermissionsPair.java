package com.deepconnect.ats.dto.auth;

import java.util.Set;

public record RolesPermissionsPair(Set<String> roleCodes, Set<String> permissionCodes) {
    
}
