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
import com.deepconnect.ats.common.enums.JwtType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtManager {
    
    @Value("${jwt.expirationMs}")
    private Long jwtExpirationMs;

    @Value("${jwt.authoritiesRolePrefix}")
    private String authoritiesRolePrefix;

    private final String tenantIdFieldName = "tenantId";
    private final String userIdFieldName = "userId";
    private final String userPublicIdFieldName = "userPublicId";
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
        RolesPermissionsPair rolePermissions = userDetails.getRolePermissionsCodes(authoritiesRolePrefix);
        return generateToken(jwtType, userDetails.getTenantId(), userDetails.getUserId(), userDetails.getPublicId(), userDetails.getEmail(), 
                userDetails.isDeactivated(), userDetails.isLocked(), rolePermissions.roleCodes(), rolePermissions.permissionCodes());
    }
    
    public String generateToken(JwtType jwtType, Long tenantId, Long userId, String userPublicId, String email, 
        boolean deactivated, boolean locked, 
        Set<String> roles, Set<String> permissions) {
        if (jwtType == JwtType.ENCRYPTED) {
            return getJwtBuilder(tenantId, userId, userPublicId, email, deactivated, locked, roles, permissions)
                .encryptWith(encryptionUtil.getEncryptionKey(), EncryptionUtil.encAlgoType)
                .compact();
        } else {
            return getJwtBuilder(tenantId, userId, userPublicId, email, deactivated, locked, roles, permissions)
            .signWith(signingUtil.getSignKey())
            .compact();
        }
    }

    private JwtBuilder getJwtBuilder(Long tenantId, Long userId, String userPublicId, String email, boolean deactivated, boolean locked, Set<String> roles, Set<String> permissions) {
        return Jwts.builder()
                .subject(email)
                .claim(tenantIdFieldName, tenantId)        // ✅ Store tenant ID in token (currently null, set appropriately)
                .claim(userIdFieldName, userId)         // ✅ Store user ID in token
                .claim(userPublicIdFieldName, userPublicId)   // ✅ Store user public ID in token (assuming email as public ID for now)
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
        ArrayList<String> roles = claims.get(rolesFieldName, ArrayList.class);
        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        }

        // Extract Permissions
        ArrayList<String> permissions = claims.get(permissionsFieldName, ArrayList.class);
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
            return new LoggedInUserDetails(
                claims.get(tenantIdFieldName, Long.class),
                claims.get(userIdFieldName, Long.class), 
                claims.get(userPublicIdFieldName, String.class), 
                claims.getSubject(),
                deactivated, 
                locked, 
                getAuthoritiesFromToken(claims));
        }
        return null;
    }

}
