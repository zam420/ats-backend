package com.deepconnect.ats.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard error body returned by the API: {"error": "...", "code": "...", "category": "...", "timestamp": "..."}.
 * "error" is the localized, user-facing message; "code" is a stable key clients can branch on.
 */
@Getter
@AllArgsConstructor
public class ErrorRsp {

    private final String error;
    private final int code;
    // private final String category;
    // private final String timestamp;

    // public static ErrorRsp of(String error, String code, ErrorCategory category) {
    //     return new ErrorRsp(error, code, category.name(), LocalDateTime.now().toString());
    // }

    public static ErrorRsp of(String error, int code) {
        return new ErrorRsp(error, code);
    }
}
