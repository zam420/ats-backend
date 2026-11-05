package com.deepconnect.ats.auth.dto;

import lombok.NoArgsConstructor;
import lombok.Data;

@Data
@NoArgsConstructor
public class LoginReq {
    private String email;
    private String password;
    // When true the refresh token lives for jwt.rememberMeRefreshExpirationMs instead of the default
    private boolean rememberMe;

    public LoginReq(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
