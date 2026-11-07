package com.deepconnect.ats.common.exception;


import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.enums.ValidationFieldType;

import lombok.Getter;

@Getter
public class ValidationException extends CustomException {

    private static String fieldKeyPrefix = "field.";

    protected final String fieldKey;

    public ValidationException(CustomExceptionType exceptionType, ValidationFieldType fieldKey, Object... args) {
        super(exceptionType.getKey(), args);
        this.fieldKey = getFullFieldKey(fieldKey.getKey());
    }

    private static String getFullFieldKey(String messageKey){
        return fieldKeyPrefix + messageKey;
    }
    
}