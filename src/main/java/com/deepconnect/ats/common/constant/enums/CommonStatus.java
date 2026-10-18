package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CommonStatus implements BaseEnum {
    ACTIVE(0, "Active"),
    INACTIVE(1, "Inactive");

    @JsonValue
    private final Integer value;
    private final String name;

    CommonStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static CommonStatus fromValue(Integer value) {
        if (value == null) return null;
        for (CommonStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
