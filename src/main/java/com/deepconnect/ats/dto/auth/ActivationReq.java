package com.deepconnect.ats.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivationReq {
    private String email;
    private String activationCode;
}
