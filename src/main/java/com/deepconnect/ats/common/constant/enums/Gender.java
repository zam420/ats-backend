package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Gender implements BaseEnum {
    MALE(0, "Male"),
    FEMALE(1, "Female"),
    PREFER_NOT_TO_SAY(2, "Prefer not to say");

    @JsonValue
    private final Integer value;
    private final String name;

    Gender(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static Gender fromValue(Integer value) {
        if (value == null) return null;
        for (Gender g : values()) {
            if (g.value.equals(value)) return g;
        }
        return null;
    }
}
