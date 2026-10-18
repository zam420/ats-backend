package com.deepconnect.ats.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.deepconnect.ats.model.User;
import com.deepconnect.ats.auth.model.RefreshToken;
import com.deepconnect.ats.service.UserService;
import com.deepconnect.ats.auth.dto.LoggedInUserDetails;
import com.deepconnect.ats.auth.dto.LoginGoogleReq;
import com.deepconnect.ats.auth.dto.LoginReq;
import com.deepconnect.ats.auth.dto.LoginRes;
import com.deepconnect.ats.auth.dto.LogoutReq;
import com.deepconnect.ats.auth.dto.TokenRefreshReq;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.enums.JwtType;
import com.deepconnect.ats.common.exception.CustomException;
import com.deepconnect.ats.common.security.JwtManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    public static LoggedInUserDetails getLoggedInUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication == null) ? null : (LoggedInUserDetails) authentication.getPrincipal();
    }

    public static Long getLoggedInUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication == null) ? null : ((LoggedInUserDetails) authentication.getPrincipal()).getUserId();
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
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUserId(), userDetails.getEmail());
        return ResponseEntity.ok(new LoginRes(userDetails.getEmail(), jwt, refreshToken.getToken(), userDetails.isDeactivated()));
    }

    public ResponseEntity<?> loginGoogle(LoginGoogleReq request){
        try {
            var payload = googleAuthService.verifyGoogleToken(request.getToken());
            User user = userService.registerOrLoginGoogle(payload.getEmail(), (String) payload.get("given_name"), (String) payload.get("family_name"));
            String jwt = jwtManager.generateToken(JwtType.ENCRYPTED, (LoggedInUserDetails)loggedInUserDetailsService.from(user));
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId(), user.getEmail());
            return ResponseEntity.ok(new LoginRes(user.getEmail(), jwt, refreshToken.getToken(), user.getDeactivated()));
        } catch (Exception ex) {
            log.error(ex.getMessage() + ex.getStackTrace().toString());
            throw new CustomException(CustomExceptionType.AUTH_GOOGLE_LOGIN_FAILED);
        }
    }

    public ResponseEntity<?> refreshToken(TokenRefreshReq request) {
        String requestRefreshToken = request.getRefreshToken();

        RefreshToken token = refreshTokenService.getByToken(requestRefreshToken);
        if(token == null || refreshTokenService.isExpired(token)){
            throw new CustomException(CustomExceptionType.AUTH_INVALID_TOKEN);
        }

        LoggedInUserDetails userDetails = (LoggedInUserDetails)loggedInUserDetailsService.loadUserByUserId(token.getUserId());
        if (!userDetails.isAccountNonLocked()) {
            // A locked user must not get new tokens; drop the session
            refreshTokenService.deleteByUserId(token.getUserId());
            throw new CustomException(CustomExceptionType.AUTH_ACCOUNT_LOCKED, HttpStatus.FORBIDDEN);
        }
        String newToken = jwtManager.generateToken(JwtType.ENCRYPTED, userDetails);
        return ResponseEntity.ok(new LoginRes(userDetails.getEmail(), newToken, requestRefreshToken, userDetails.isDeactivated()));
    }

    public ResponseEntity<?> logout(LogoutReq request) {
        refreshTokenService.deleteByToken(request.getRefreshToken());
        return ResponseEntity.ok("");
    }

}
