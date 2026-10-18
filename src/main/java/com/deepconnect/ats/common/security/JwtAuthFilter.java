package com.deepconnect.ats.common.security;
import com.deepconnect.ats.common.enums.JwtType;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.exception.GlobalExceptionHandler;
import org.springframework.http.HttpStatus;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtManager jwtManager;
    private final GlobalExceptionHandler globalExceptionHandler;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtManager.extractTokenFromAuthHeader(request.getHeader("Authorization"));
        if (token != null) {
            LoggedInUserDetails userDetails = jwtManager.getUserDetails(JwtType.ENCRYPTED, token);
            if (userDetails != null) {
                // Locked/unactivated state comes from the token claims (no DB call). It can lag by up to the
                // token lifetime; /auth/refresh and login always re-read it from the DB.
                // Unactivated accounts may only call /auth/** (activate, refresh, logout).
                boolean blockedPath = !request.getServletPath().startsWith("/auth/");
                CustomException blocked = null;
                if (userDetails.isLocked()) {
                    blocked = new CustomException(CustomExceptionType.AUTH_ACCOUNT_LOCKED, HttpStatus.FORBIDDEN);
                } else if (userDetails.isDeactivated() && blockedPath) {
                    blocked = new CustomException(CustomExceptionType.AUTH_ACCOUNT_NOT_ACTIVATED, HttpStatus.FORBIDDEN);
                }
                if (blocked != null) {
                    SecurityContextHolder.clearContext();
                    globalExceptionHandler.setCustomExceptionInResponse(blocked, request, response);
                    return;
                }
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }

}
