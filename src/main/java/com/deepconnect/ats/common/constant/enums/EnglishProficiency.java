package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum EnglishProficiency implements BaseEnum {
    GENERAL(0, "General"),
    GOOD(1, "Good"),
    PROFICIENT(2, "Proficient"),
    CAN_BE_USED_AS_WORKING_LANGUAGE(3, "Can be used as working language");

    @JsonValue
    private final Integer value;
    private final String name;

    EnglishProficiency(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static EnglishProficiency fromValue(Integer value) {
        if (value == null) return null;
        for (EnglishProficiency e : values()) {
            if (e.value.equals(value)) return e;
        }
        return null;
    }
}
