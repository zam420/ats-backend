package com.deepconnect.ats.common.exception;

import org.springframework.http.HttpStatus;

import com.deepconnect.ats.common.enums.CustomExceptionType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * * Used when a valid logged-in user attempts to access a resource they are not authorized to access.
 * ForbiddenException
 */
@Slf4j
@Getter
public class ForbiddenException extends CustomException {

    //TODO: This exception should receive an args parameter indicating which user attempted to access what resource
    //TODO: Add a handler in GlobalExceptionHandler for this. So far this exception is not used anywhere.
    public ForbiddenException(CustomExceptionType exceptionType) {
        super(exceptionType, HttpStatus.FORBIDDEN);
    }
    
}