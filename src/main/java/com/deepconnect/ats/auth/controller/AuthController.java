package com.deepconnect.ats.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.deepconnect.ats.user.service.UserServiceV2;
import com.deepconnect.ats.auth.dto.ActivationReq;
import com.deepconnect.ats.auth.dto.ForgotPasswordReq;
import com.deepconnect.ats.auth.dto.LoginGoogleReq;
import com.deepconnect.ats.auth.dto.LoginReq;
import com.deepconnect.ats.auth.dto.RegisterReq;
import com.deepconnect.ats.auth.dto.ResetPasswordReq;
import com.deepconnect.ats.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserServiceV2 userService;
    private final AuthService authService;

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterReq request) {
        //if(true){
            //throw new CustomException(CustomExceptionType.TEST_CUSTOM_WITH_ARGS, "zam0", "zam1");
            //throw new ValidationRangeException(ValidationFieldType.LATITUDE, -90, 90);
        //}
        userService.register(request);
        return authService.login(new LoginReq(request.getEmail(), request.getPassword()));
    }

    @Transactional 
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginReq request) {
        return authService.login(request);
    }

    @Transactional
    @PostMapping("/logingoogle")
    public ResponseEntity<?> loginGoogle(@RequestBody LoginGoogleReq request) {
        return authService.loginGoogle(request);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refresh_token", required = false) String refreshToken) {
        return authService.logout(refreshToken);
    }

    @Transactional
    @PostMapping("/forgotPassword")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordReq request) {
        userService.forgotPassword(request);
        return ResponseEntity.ok("");
    }

    @Transactional
    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordReq request) {
        userService.resetPassword(request);
        return ResponseEntity.ok("");
    }

    @Transactional
    @PostMapping("/activate")
    public ResponseEntity<?> activateAccount(@RequestBody ActivationReq request) {
        userService.activate(request);
        return ResponseEntity.ok("");
    }

}
