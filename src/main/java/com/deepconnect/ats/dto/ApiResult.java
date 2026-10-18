package com.deepconnect.ats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {

    public static final int SUCCESS_CODE = 1000;
    public static final int FAILED_CODE = 1001;
    public static final int UNAUTHORIZED_CODE = 1002;
    public static final int FORBIDDEN_CODE = 1003;
    public static final int PARAMS_ERROR_CODE = 1005;

    @Builder.Default
    private int code = SUCCESS_CODE;

    @Builder.Default
    private String message = "success";

    @Builder.Default
    private boolean success = true;

    private T result;

    public static <T> ApiResult<T> success() {
        return ApiResult.<T>builder().build();
    }

    public static <T> ApiResult<T> success(T result) {
        return ApiResult.<T>builder()
                .result(result)
                .build();
    }

    public static <T> ApiResult<T> fail(String message) {
        return ApiResult.<T>builder()
                .code(FAILED_CODE)
                .message(message)
                .success(false)
                .build();
    }

    public static <T> ApiResult<T> fail(int code, String message) {
        return ApiResult.<T>builder()
                .code(code)
                .message(message)
                .success(false)
                .build();
    }
}
