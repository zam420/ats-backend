package com.deepconnect.ats.common.exception;
import org.springframework.http.HttpStatus;

import com.deepconnect.ats.common.enums.CustomExceptionType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InternalServerException extends LoggedCustomException {

    public InternalServerException(String logMessage) {
        super(CustomExceptionType.INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, logMessage);
    }
    
}