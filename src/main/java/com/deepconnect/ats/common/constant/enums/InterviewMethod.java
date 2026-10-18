package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum InterviewMethod implements BaseEnum {
    WRITTEN_EXAM(0, "Written exam"),
    PHONE(1, "Phone"),
    VIDEO(2, "Video"),
    ON_SITE(3, "On-site");

    @JsonValue
    private final Integer value;
    private final String name;

    InterviewMethod(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static InterviewMethod fromValue(Integer value) {
        if (value == null) return null;
        for (InterviewMethod m : values()) {
            if (m.value.equals(value)) return m;
        }
        return null;
    }
}
