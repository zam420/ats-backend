package com.deepconnect.ats.common.exception;

import org.springframework.http.HttpStatus;

import com.deepconnect.ats.common.enums.CustomExceptionType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Used when we want to log the exception with a custom log message.
 * LoggedCustomException
 */
@Slf4j
@Getter
public class LoggedCustomException extends CustomException {

    public final String logMessage;

    public LoggedCustomException(CustomExceptionType exceptionType, HttpStatus httpStatus, String logMessage) {
        super(exceptionType, httpStatus);
        this.logMessage = logMessage;
    }
    
}