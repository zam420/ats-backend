package com.deepconnect.ats.dto.auth;

import lombok.Data;

@Data
public class TokenRefreshReq {

    private String refreshToken;

}
