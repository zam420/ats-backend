package com.deepconnect.ats.common.exception;

import org.springframework.http.HttpStatus;

import com.deepconnect.ats.common.enums.CustomExceptionType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Used when a user is not authenticated or their authentication is invalid (e.g. locked/deactivated/expired token)
 * UnauthorizedException
 */
@Slf4j
@Getter
public class UnauthorizedException extends LoggedCustomException {

    public UnauthorizedException(CustomExceptionType exceptionType, String userIdOrEmail) {
        super(exceptionType, HttpStatus.UNAUTHORIZED, "User: " + userIdOrEmail);
    }
    
}