package com.deepconnect.ats.common.exception;

import org.springframework.http.HttpStatus;

import com.deepconnect.ats.common.enums.CustomExceptionType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super(CustomExceptionType.AUTH_INVALID, HttpStatus.UNAUTHORIZED);
    }
    
}