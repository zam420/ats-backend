package com.deepconnect.ats.common.enums;

import lombok.Getter;

@Getter
public enum EmailType {
    ACCOUNT_ACTIVATION("account.activation", "account_activation"),
    ACCOUNT_PASSWORD_RESET("account.passwordReset", "account_passwordReset")
    ;

    private final String subjectKey;
    private final String templateKey;

    EmailType(String subjectKey, String templateKey) {
        this.subjectKey = subjectKey;
        this.templateKey = templateKey;
    }
}
