package com.deepconnect.ats.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class LoginRes {

    private String email;
    private String accessToken;
    private String refreshToken;
    private boolean activationRequired;

    public LoginRes(String email, String accessToken, String refreshToken) {
        this(email, accessToken, refreshToken, false);
    }

    public LoginRes(String email, String accessToken, String refreshToken, boolean activationRequired) {
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.activationRequired = activationRequired;
    }

}

