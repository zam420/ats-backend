package com.deepconnect.ats.common.enums;

import lombok.Getter;

@Getter
public enum CustomExceptionType {
    TEST_CUSTOM("testcustom"),
    TEST_CUSTOM_WITH_ARGS("customwithargs"),
    INTERNAL_SERVER_ERROR("internalServerError"),
    GOOGLE_CONNECTION_ERROR("googleConnectionError"),
        
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

    DUPLICATE_NAME("duplicateName"),
    DUPLICATE_NAME_WITHIN_TENANT("duplicateNameWithinTenant"),

    VALIDATION_RANGE("validation.range"),
    VALIDATION_MUST_ADD("validation.mustAdd"),
    ;

    private final String key;

    CustomExceptionType(String key) {
        this.key = key;
    }
}
