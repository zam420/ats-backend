package com.deepconnect.ats.auth.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.deepconnect.ats.service.UserService;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.dto.RolesPermissionsPair;
import com.deepconnect.ats.common.exception.UnauthorizedException;
import com.deepconnect.ats.model.User;
import com.deepconnect.ats.rbac.model.Role;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoggedInUserDetailsService implements UserDetailsService {

    @Value("${jwt.authoritiesRolePrefix}")
    private static String authoritiesRolePrefix;
    
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new UnauthorizedException());
        return from(user);
    }

    public UserDetails loadUserByUserId(Long userId) {
        User user = userService.findByUserId(userId).orElseThrow(() -> new UnauthorizedException());
        return from(user);
    }

    public UserDetails from(User user) {
        Set<Role> roles = userService.getRolesByUserId(user.getId());
        List<GrantedAuthority> authorities = new ArrayList<>();
        Set<Long> roleIds = new HashSet<>();
        for (Role role : roles) {
            // Spring Security explicitly expects the ROLE_ prefix when you use the hasRole('ADMIN') expression.
            authorities.add(new SimpleGrantedAuthority(authoritiesRolePrefix + role.getCode()));
            roleIds.add(role.getId());
        }
        Set<String> permissionCodes = userService.getPermissionCodesByRoleIds(roleIds);
        for (String permissionCode : permissionCodes) {
            authorities.add(new SimpleGrantedAuthority(permissionCode));
        }
        return new LoggedInUserDetails(user, authorities);
    }

    public static RolesPermissionsPair getRolePermissionsCodes(Collection<? extends GrantedAuthority> authorities) {
        Set<String> roleCodes = new HashSet<>();
        Set<String> permissionCodes = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            if (authority.getAuthority().startsWith(authoritiesRolePrefix)) {
                roleCodes.add(authority.getAuthority());
            } else {
                permissionCodes.add(authority.getAuthority());
            }
        }
        return new RolesPermissionsPair(roleCodes, permissionCodes);
    }


    

}
