package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum UserType implements BaseEnum {
    CORPORATE_USER(0, "Corporate User"),
    CANDIDATE(1, "Candidate"),
    VIRTUAL_USER(2, "System Virtual User");

    @JsonValue
    private final Integer value;
    private final String name;

    UserType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static UserType fromValue(Integer value) {
        if (value == null) return null;
        for (UserType u : values()) {
            if (u.value.equals(value)) return u;
        }
        return null;
    }
}
