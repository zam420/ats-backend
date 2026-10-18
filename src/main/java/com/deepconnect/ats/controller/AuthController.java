package com.deepconnect.ats.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import com.deepconnect.ats.dto.auth.LoginReq;
import com.deepconnect.ats.service.UserService;
import com.deepconnect.ats.service.auth.AuthService;
import com.deepconnect.ats.dto.auth.ActivationReq;
import com.deepconnect.ats.dto.auth.ForgotPasswordReq;
import com.deepconnect.ats.dto.auth.ResetPasswordReq;
import com.deepconnect.ats.dto.auth.LoginGoogleReq;
import com.deepconnect.ats.dto.auth.LogoutReq;
import com.deepconnect.ats.dto.auth.RegisterReq;
import com.deepconnect.ats.dto.auth.TokenRefreshReq;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
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
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshReq request) {
        return authService.refreshToken(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutReq request) {
        return authService.logout(request);
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
