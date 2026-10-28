package com.deepconnect.ats.common.security;
import com.deepconnect.ats.common.enums.JwtType;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.CustomException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

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
import org.springframework.web.servlet.HandlerExceptionResolver;
import com.deepconnect.ats.common.exception.UnauthorizedException;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtManager jwtManager;

    
    /* In GlobalExceptionHandler, @ControllerAdvice does not apply to filters because it only works for exceptions thrown inside controllers.
    * So any errors encountered inside the filters such as JwtAuthenticationFilter are not handled automatically by GlobalExceptionHandler. 
    * This also happens because in Spring boot, filters do not automatically propagate exceptions to @ControllerAdvice.
    * To solve this, we use HandlerExceptionResolver. */
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = jwtManager.extractTokenFromAuthHeader(request.getHeader("Authorization"));
            if(token != null) {
                LoggedInUserDetails userDetails = jwtManager.getUserDetails(JwtType.ENCRYPTED, token);
                if(userDetails == null) {
                    throw new UnauthorizedException(CustomExceptionType.AUTH_INVALID_TOKEN, "Expired");
                }
                
                // Locked/unactivated state comes from the token claims (no DB call). It can lag by up to the
                // token lifetime; /auth/refresh and login always re-read it from the DB.
                // Unactivated accounts may only call /auth/** (activate, refresh, logout).
                boolean blockedPath = !request.getServletPath().startsWith("/api/auth/");
                CustomException userBlockedException = null;
                if (userDetails.isLocked()) {
                    userBlockedException = new UnauthorizedException(CustomExceptionType.AUTH_ACCOUNT_LOCKED, userDetails.getEmail());
                } else if (userDetails.isDeactivated() && blockedPath) {
                    userBlockedException = new UnauthorizedException(CustomExceptionType.AUTH_ACCOUNT_NOT_ACTIVATED, userDetails.getEmail());
                }
                if (userBlockedException != null) {
                    SecurityContextHolder.clearContext();
                    throw userBlockedException;
                }
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            resolver.resolveException(request, response, null, ex);
        }
    }

}
