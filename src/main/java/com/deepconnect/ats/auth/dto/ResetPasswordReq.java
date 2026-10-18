package com.deepconnect.ats.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordReq {
    private String email;
    private String resetCode;
    private String newPassword;
}
