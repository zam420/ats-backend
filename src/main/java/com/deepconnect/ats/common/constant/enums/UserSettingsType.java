package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserSettingsType implements BaseEnum {
    CV_MANAGEMENT_QUERY(0, "CV Management Query");

    @JsonValue
    private final Integer value;
    private final String name;

    UserSettingsType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static UserSettingsType fromValue(Integer value) {
        if (value == null) return null;
        for (UserSettingsType s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
