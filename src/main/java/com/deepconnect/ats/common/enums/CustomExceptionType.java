package com.deepconnect.ats.common.enums;

import lombok.Getter;

@Getter
public enum CustomExceptionType {
    TEST_CUSTOM("testcustom"),
    TEST_CUSTOM_WITH_ARGS("customwithargs"),
    INTERNAL_SERVER_ERROR("internalServerError"),
    GOOGLE_CONNECTION_ERROR("googleConnectionError"),
    VALIDATION_RANGE("validation.range"),
    MEMORY_TYPE_BLANK("memoryTypeBlank"),
    MEMORY_CONTENT_BLANK("memoryContentBlank"),
    
    AUTH_INVALID("authInvalid"),
    AUTH_INVALID_TOKEN("authInvalidToken"),
    AUTH_INVALID_API_KEY("authInvalidAPIKey"),
    AUTH_INVALID_ACTIVATION_CODE("authInvalidActivationCode"),
    AUTH_ACCOUNT_LOCKED("authAccountLocked"),
    AUTH_ACCOUNT_NOT_ACTIVATED("authAccountNotActivated"),
    AUTH_GOOGLE_LOGIN_FAILED("authGoogleLoginFailed"),
    AUTH_INVALID_RESET_CODE("authInvalidResetCode"),
    AUTH_RESET_EMAIL_FAILURE("authResetEmailFailure"),

    REGISTER_USERNAME_IN_USE("registerUserNameInUse"),
    REGISTER_EMAIL_IN_USE("registerEmailInUse"),
    REGISTER_ACTIVATION_EMAIL_FAILURE("registerActivationEmailFailure"),
    
    ;

    private final String key;

    CustomExceptionType(String key) {
        this.key = key;
    }
}
