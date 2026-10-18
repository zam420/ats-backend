package com.deepconnect.ats.common.security;

import java.util.Set;
import java.util.HashSet;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.dto.RolesPermissionsPair;
import com.deepconnect.ats.auth.service.LoggedInUserDetailsService;
import com.deepconnect.ats.common.enums.JwtType;

import java.util.Collection;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtManager {
    
    @Value("${jwt.expirationMs}")
    private Long jwtExpirationMs;

    @Value("${jwt.authoritiesRolePrefix}")
    private String authoritiesRolePrefix;

    private final String userIdFieldName = "userId";
    private final String rolesFieldName = "roles";
    private final String deactivatedFieldName = "deactivated";
    private final String lockedFieldName = "locked";
    private final String permissionsFieldName = "permissions";

    private final SigningUtil signingUtil;
    private final EncryptionUtil encryptionUtil; 

    public String extractTokenFromAuthHeader(String authHeader){
        if (authHeader != null && authHeader.toLowerCase().startsWith("bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public String generateToken(JwtType jwtType, LoggedInUserDetails userDetails) {
        RolesPermissionsPair rolePermissions = LoggedInUserDetailsService.getRolePermissionsCodes(userDetails.getAuthorities());
        return generateToken(jwtType, userDetails.getUserId(), userDetails.getEmail(), 
                userDetails.isDeactivated(), userDetails.isLocked(), rolePermissions.roleCodes(), rolePermissions.permissionCodes());
    }
    
    public String generateToken(JwtType jwtType, Long userId, String email, 
        boolean deactivated, boolean locked, 
        Set<String> roles, Set<String> permissions) {
        if (jwtType == JwtType.ENCRYPTED) {
            return getJwtBuilder(userId, email, deactivated, locked, roles, permissions)
                .encryptWith(encryptionUtil.getEncryptionKey(), EncryptionUtil.encAlgoType)
                .compact();
        } else {
            return getJwtBuilder(userId, email, deactivated, locked, roles, permissions)
            .signWith(signingUtil.getSignKey())
            .compact();
        }
    }

    private JwtBuilder getJwtBuilder(Long userId, String email, boolean deactivated, boolean locked, Set<String> roles, Set<String> permissions) {
        return Jwts.builder()
                .subject(email)
                .claim(userIdFieldName, userId)         // ✅ Store user ID in token
                .claim(rolesFieldName, roles)          // e.g., ["ROLE_ADMIN", "ROLE_USER"]
                .claim(permissionsFieldName, permissions)     // e.g., ["PERM_READ", "PERM_WRITE"]
                .claim(deactivatedFieldName, deactivated)
                .claim(lockedFieldName, locked)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs));
    }

    public Claims extractAllClaims(JwtType jwtType, String token) {
        try{
            if (jwtType == JwtType.ENCRYPTED) {
                return Jwts.parser()
                    .decryptWith(encryptionUtil.getEncryptionKey())
                    .build()
                    .parseEncryptedClaims(token)
                    .getPayload();
            } else {
                return Jwts.parser()
                    .verifyWith(signingUtil.getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            }
            
        }
        catch(ExpiredJwtException ex){
            return null;
        }
    }

    public boolean isTokenValid(Claims claims) {
        return claims.getExpiration().after(new Date());
    }

    public Collection<? extends GrantedAuthority> getAuthoritiesFromToken(Claims claims) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();

        // Extract Roles (Note that roles have the prefix "ROLE_" while permisions don't have any)
        Set<String> roles = claims.get(rolesFieldName, Set.class);
        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        }

        // Extract Permissions
        Set<String> permissions = claims.get(permissionsFieldName, Set.class);
        if (permissions != null) {
            permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        }
        
        return authorities;
    }

    public LoggedInUserDetails getUserDetails(JwtType jwtType, String token) {
        Claims claims = this.extractAllClaims(jwtType, token);
        if(claims != null && isTokenValid(claims)){
            // Tokens issued before these claims existed are treated as active/unlocked
            boolean deactivated = Boolean.TRUE.equals(claims.get(deactivatedFieldName, Boolean.class));
            boolean locked = Boolean.TRUE.equals(claims.get(lockedFieldName, Boolean.class));
            return new LoggedInUserDetails(claims.get(userIdFieldName, Long.class), claims.getSubject(),
                    deactivated, locked, getAuthoritiesFromToken(claims));
        }
        return null;
    }

}
