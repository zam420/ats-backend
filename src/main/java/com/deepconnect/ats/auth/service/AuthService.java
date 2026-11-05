package com.deepconnect.ats.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.deepconnect.ats.common.security.SecurityConfig;

import com.deepconnect.ats.user.model.User;
import com.deepconnect.ats.auth.model.RefreshToken;
import com.deepconnect.ats.user.service.UserService;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.dto.LoginGoogleReq;
import com.deepconnect.ats.auth.dto.LoginReq;
import com.deepconnect.ats.auth.dto.LoginRes;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.enums.JwtType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.security.JwtManager;
import com.deepconnect.ats.common.exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtManager jwtManager;
    private final LoggedInUserDetailsService loggedInUserDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final GoogleAuthService googleAuthService;
    private final UserService userService;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshExpirationMs;

    // TODO: Must be true when the site is served over https
    @Value("${auth.cookieSecure:false}")
    private boolean cookieSecure;

    @Value("${auth.refreshCookieName}")
    private String refreshCookieName;

    private ResponseCookie refreshCookie(String value, long maxAgeMs) {
        return ResponseCookie.from(refreshCookieName, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(SecurityConfig.AUTH_ENDPOINT_BASE)// The cookie is only sent to the auth endpoints, never with ordinary API calls
                .maxAge(Duration.ofMillis(maxAgeMs))
                .build();
    }

    // The cookie lives exactly as long as the stored token, so "remember me" tokens get their longer lifetime
    private ResponseEntity<LoginRes> withRefreshCookie(LoginRes res, RefreshToken token) {
        long maxAgeMs = Math.max(0, Duration.between(LocalDateTime.now(), token.getExpiryDate()).toMillis());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(res.getRefreshToken(), maxAgeMs).toString())
                .body(res);
    }

    public static LoggedInUserDetails getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication == null) ? null : (LoggedInUserDetails) authentication.getPrincipal();
    }

    public static Long getLoggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication == null) ? null : ((LoggedInUserDetails) authentication.getPrincipal()).getUserId();
    }

    public static String getLoggedInPublicId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication == null) ? null : ((LoggedInUserDetails) authentication.getPrincipal()).getPublicId();
    }

    public static Long getLoggedInTenantId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication == null) ? null : ((LoggedInUserDetails) authentication.getPrincipal()).getTenantId();
    }

    public ResponseEntity<?> login(LoginReq request) {
        //In SecurityConfig.java, method authManager, we have associated CustomUserDetailsService with global authentication manager.
        //So when we call authenticationManager.authenticate method below, it will automatically call method loadUserByUsername in CustomUserDetailsService.java.
        Authentication usernamePasswordAuthentication = new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //loadUserByUsername in CustomUserDetailsService.java returns data of type UserDetails so authenticaion.getPrincipal is of type UserDetails 
        LoggedInUserDetails userDetails = (LoggedInUserDetails) authentication.getPrincipal();

        String jwt = jwtManager.generateToken(JwtType.ENCRYPTED, userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUserId(), userDetails.getEmail(), request.isRememberMe());
        return withRefreshCookie(new LoginRes(userDetails.getEmail(), jwt, refreshToken.getToken(), userDetails.isDeactivated()), refreshToken);
    }

    public ResponseEntity<?> loginGoogle(LoginGoogleReq request){
        try {
            var payload = googleAuthService.verifyGoogleToken(request.getToken());
            User user = userService.registerOrLoginGoogle(payload.getEmail(), (String) payload.get("given_name"), (String) payload.get("family_name"));
            String jwt = jwtManager.generateToken(JwtType.ENCRYPTED, (LoggedInUserDetails)loggedInUserDetailsService.from(user));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId(), user.getEmail());
            return withRefreshCookie(new LoginRes(user.getEmail(), jwt, refreshToken.getToken(), user.getDeactivated()), refreshToken);
        } catch (Exception ex) {
            log.error(ex.getMessage() + ex.getStackTrace().toString());
            throw new CustomException(CustomExceptionType.AUTH_GOOGLE_LOGIN_FAILED);
        }
    }

    public ResponseEntity<?> refreshToken(String requestRefreshToken) {
        if (requestRefreshToken == null || requestRefreshToken.isBlank()) {
            throw new UnauthorizedException(CustomExceptionType.AUTH_INVALID_TOKEN, "Refresh token missing");
        }

        RefreshToken token = refreshTokenService.getByToken(requestRefreshToken);
        if(token == null || refreshTokenService.isExpired(token)){//refresh token is also generated at login time. So if it's missing, the user is not authenticated
            throw new UnauthorizedException(CustomExceptionType.AUTH_INVALID_TOKEN, "Refresh expired");
        }

        LoggedInUserDetails userDetails = (LoggedInUserDetails)loggedInUserDetailsService.loadUserByUsername(token.getEmail());
        if (!userDetails.isAccountNonLocked()) {
            // A locked user must not get new tokens; drop the session
            refreshTokenService.deleteByUserId(token.getUserId());
            throw new UnauthorizedException(CustomExceptionType.AUTH_ACCOUNT_LOCKED, userDetails.getEmail());
        }
        else if (userDetails.isDeactivated()) {
            //don't issue new tokens for deactivated accounts. drop the session
            throw new UnauthorizedException(CustomExceptionType.AUTH_ACCOUNT_NOT_ACTIVATED, userDetails.getEmail());
        }
        String newToken = jwtManager.generateToken(JwtType.ENCRYPTED, userDetails);
        return withRefreshCookie(new LoginRes(userDetails.getEmail(), newToken, requestRefreshToken, userDetails.isDeactivated()), token);
    }

    public ResponseEntity<?> logout(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            refreshTokenService.deleteByToken(refreshToken);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie("", 0).toString())
                .body("");
    }

}
