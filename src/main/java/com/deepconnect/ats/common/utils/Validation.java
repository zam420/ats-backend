package com.deepconnect.ats.common.utils;

import com.deepconnect.ats.common.enums.ValidationFieldType;
import com.deepconnect.ats.common.enums.CustomExceptionType;
import com.deepconnect.ats.common.exception.ValidationException;

public class Validation {

    public static void validateRange(ValidationFieldType validationFieldType, int rangeFrom, int rangeTo, int valueToValidate) {
        if (!(valueToValidate >= rangeFrom && valueToValidate <= rangeTo)) {
            throw new ValidationException(CustomExceptionType.VALIDATION_RANGE, validationFieldType, rangeFrom, rangeTo);
        }
    }

    public static void validateRange(ValidationFieldType validationFieldType, double rangeFrom, double rangeTo, double valueToValidate) {
        if (!(valueToValidate >= rangeFrom && valueToValidate <= rangeTo)) {
            throw new ValidationException(CustomExceptionType.VALIDATION_RANGE, validationFieldType, rangeFrom, rangeTo);
        }
    }
    
}
