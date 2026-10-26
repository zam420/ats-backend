package com.deepconnect.ats.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.common.enums.PermissionType;
import com.deepconnect.ats.common.enums.RoleType;

@Data 
public class LoggedInUserDetails implements UserDetails {

    private final static String ROLE_PREFIX = "ROLE_";

    private final Long tenantId;
    private final Long userId;
    private final String publicId;
    private final String username;
    private final String email;
    private final String password;
    private final boolean locked;
    private final boolean deactivated;//used for newly registered users as their accounts will be deactivated at first.
    private final boolean credentialsExpired;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Set<String> roles;//extracted from authorities with the role prefix
    private final Set<String> permissions;//extracted from authorities without the role prefix

    // Constructor for database authentication
    public LoggedInUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        this.tenantId = user.getTenantId();
        this.userId = user.getId();
        this.publicId = user.getPublicId();
        this.username = user.getFullName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = authorities;//this includes roles only
        RolesPermissionsPair pair = getRolePermissionsCodes(ROLE_PREFIX);
        this.roles = pair.roleCodes();
        this.permissions = pair.permissionCodes();
        this.locked = user.getLocked(); 
        this.deactivated = user.getDeactivated(); 
        this.credentialsExpired = user.getCredentialsExpired(); 
    }

    public LoggedInUserDetails(Long tenantId, Long userId, String publicId, String email, 
            boolean deactivated, boolean locked, Collection<? extends GrantedAuthority> authorities) {
        this.tenantId = tenantId;
        this.userId = userId;
        this.publicId = publicId;
        this.username = null;
        this.email = email;
        this.password = null;
        this.authorities = authorities;//this includes both roles and permissions`
        RolesPermissionsPair pair = getRolePermissionsCodes(ROLE_PREFIX);
        this.roles = pair.roleCodes();
        this.permissions = pair.permissionCodes();
        this.locked = locked;
        this.deactivated = deactivated;
        this.credentialsExpired = false; //TODO: user.getCredentialsExpired();
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !credentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public RolesPermissionsPair getRolePermissionsCodes(String authoritiesRolePrefix) {
        Set<String> roleCodes = new HashSet<>();
        Set<String> permissionCodes = new HashSet<>();
        for (GrantedAuthority authority : this.authorities) {
            if (authority.getAuthority().startsWith(authoritiesRolePrefix)) {
                roleCodes.add(authority.getAuthority());
            } else {
                permissionCodes.add(authority.getAuthority());
            }
        }
        return new RolesPermissionsPair(roleCodes, permissionCodes);
    }

    public boolean hasPermission(PermissionType permissionType) {
        return this.hasPermission(permissionType.getCode());
    }

    public boolean hasPermission(String permissionCode) {
        return this.permissions.contains(permissionCode);
    }

    public boolean hasRole(RoleType roleType) {
        return this.hasRole(roleType.getCode());
    }

    public boolean hasRole(String roleCode) {
        return this.roles.contains(roleCode);
    }

}
