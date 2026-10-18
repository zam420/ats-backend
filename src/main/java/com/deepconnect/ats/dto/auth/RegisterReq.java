package com.deepconnect.ats.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterReq {
    private String password;
    private String email;

    private String firstName;
    private String lastName;
}
