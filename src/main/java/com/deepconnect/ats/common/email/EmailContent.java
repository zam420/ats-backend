package com.deepconnect.ats.common.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailContent {
    private String subject;
    private String body;
}
