package com.deepconnect.ats.common.utils;

import com.deepconnect.ats.common.enums.ValidationFieldType;
import com.deepconnect.ats.common.exception.ValidationRangeException;

public class Validation {

    public static void validateRange(ValidationFieldType validationFieldType, int rangeFrom, int rangeTo, int valueToValidate) {
        if (!(valueToValidate >= rangeFrom && valueToValidate <= rangeTo)) {
            throw new ValidationRangeException(validationFieldType, rangeFrom, rangeTo);
        }
    }

    public static void validateRange(ValidationFieldType validationFieldType, double rangeFrom, double rangeTo, double valueToValidate) {
        if (!(valueToValidate >= rangeFrom && valueToValidate <= rangeTo)) {
            throw new ValidationRangeException(validationFieldType, rangeFrom, rangeTo);
        }
    }
    
}
