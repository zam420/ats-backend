package com.deepconnect.ats.common.exception;

import org.springframework.http.HttpStatus;

import com.deepconnect.ats.common.enums.CustomExceptionType;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private static String customExceptionKeyPrefix = "exception.";

    protected final String messageKey;
    protected final Object[] args;
    protected final HttpStatus httpStatus;

    protected CustomException(String messageKey, HttpStatus httpStatus, Object... args) {
        this.messageKey = messageKey;
        this.args = args;
        this.httpStatus = httpStatus;
    }

    protected CustomException(String messageKey) {
        this(messageKey, HttpStatus.BAD_REQUEST, (Object[])null);
    }

    protected CustomException(String messageKey, Object... args) {
        this(messageKey, HttpStatus.BAD_REQUEST, args);
    }

    protected CustomException(String messageKey, HttpStatus httpStatus) {
        this(messageKey, httpStatus, (Object[])null);
    }

    public CustomException(CustomExceptionType exceptionType) {
        this(getFullMessageKey(exceptionType.getKey()));
    }

    public CustomException(CustomExceptionType exceptionType, Object... args) {
        this(getFullMessageKey(exceptionType.getKey()), args);
    }

    public CustomException(CustomExceptionType exceptionType, HttpStatus httpStatus) {
        this(getFullMessageKey(exceptionType.getKey()), httpStatus, (Object[])null);
    }

    private static String getFullMessageKey(String messageKey){
        return customExceptionKeyPrefix + messageKey;
    }
    
}