package com.deepconnect.ats.dto.auth;

import lombok.Data;

@Data
public class LogoutReq {

    private String refreshToken;
    
}
