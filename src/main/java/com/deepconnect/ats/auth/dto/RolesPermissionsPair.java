package com.deepconnect.ats.auth.dto;

import java.util.Set;

public record RolesPermissionsPair(Set<String> roleCodes, Set<String> permissionCodes) {
    
}
