package com.deepconnect.ats.common.exception;


import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.enums.ValidationFieldType;

import lombok.Getter;

@Getter
public class ValidationRangeException extends CustomException {

    private static String fieldKeyPrefix = "field.";

    protected final String fieldKey;

    public ValidationRangeException(ValidationFieldType fieldKey, Object... args) {
        super(CustomExceptionType.VALIDATION_RANGE, args);
        this.fieldKey = getFullFieldKey(fieldKey.getKey());
    }

    private static String getFullFieldKey(String messageKey){
        return fieldKeyPrefix + messageKey;
    }
    
}