package com.deepconnect.ats.common.exception;

import org.springframework.http.HttpStatus;

import com.deepconnect.ats.common.enums.CustomExceptionType;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class GoogleConnectionException extends LoggedCustomException {

    public GoogleConnectionException(String logMessage) {
        super(CustomExceptionType.GOOGLE_CONNECTION_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, logMessage);
    }
    
}