package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum JobPositionLevel implements BaseEnum {
    JUNIOR(0, "Junior"),
    MIDDLE(1, "Middle"),
    SENIOR(2, "Senior"),
    EXPERT(3, "Expert"),
    ALL(4, "All");

    @JsonValue
    private final Integer value;
    private final String name;

    JobPositionLevel(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static JobPositionLevel fromValue(Integer value) {
        if (value == null) return null;
        for (JobPositionLevel l : values()) {
            if (l.value.equals(value)) return l;
        }
        return null;
    }
}
