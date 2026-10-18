package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MaritalStatus implements BaseEnum {
    UNMARRIED(0, "Unmarried"),
    MARRIED(1, "Married"),
    DIVORCED(2, "Divorced"),
    WIDOWED(3, "Widowed"),
    PREFER_NOT_TO_SAY(4, "Prefer not to say");

    @JsonValue
    private final Integer value;
    private final String name;

    MaritalStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static MaritalStatus fromValue(Integer value) {
        if (value == null) return null;
        for (MaritalStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
