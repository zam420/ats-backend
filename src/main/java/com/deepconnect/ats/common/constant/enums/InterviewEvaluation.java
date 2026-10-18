package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum InterviewEvaluation implements BaseEnum {
    EXCELLENT(0, "Excellent"),
    GOOD(1, "Good"),
    AVERAGE(2, "Average"),
    POOR(3, "Poor");

    @JsonValue
    private final Integer value;
    private final String name;

    InterviewEvaluation(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static InterviewEvaluation fromValue(Integer value) {
        if (value == null) return null;
        for (InterviewEvaluation e : values()) {
            if (e.value.equals(value)) return e;
        }
        return null;
    }
}
