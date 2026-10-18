package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum WorkExperience implements BaseEnum {
    GRADUATING_STUDENTS(0, "Graduating students"),
    WITHIN_1_YEAR(1, "Within 1 year"),
    ONE_TO_THREE_YEARS(2, "1-3 years"),
    THREE_TO_FIVE_YEARS(3, "3-5 years"),
    FIVE_TO_TEN_YEARS(4, "5-10 years"),
    MORE_THAN_TEN_YEARS(5, "More than 10 years"),
    NO_LIMIT(6, "No limit");

    @JsonValue
    private final Integer value;
    private final String name;

    WorkExperience(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static WorkExperience fromValue(Integer value) {
        if (value == null) return null;
        for (WorkExperience w : values()) {
            if (w.value.equals(value)) return w;
        }
        return null;
    }
}
