package com.deepconnect.ats.auth.dto;

import lombok.Data;

@Data
public class LogoutReq {

    private String refreshToken;
    
}
