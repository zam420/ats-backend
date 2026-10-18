package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Degree implements BaseEnum {
    JUNIOR_COLLEGE(0, "Junior college"),
    BACHELOR(1, "Bachelor"),
    MASTER(2, "Master"),
    PHD(3, "PhD"),
    MBA(4, "MBA");

    @JsonValue
    private final Integer value;
    private final String name;

    Degree(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static Degree fromValue(Integer value) {
        if (value == null) return null;
        for (Degree d : values()) {
            if (d.value.equals(value)) return d;
        }
        return null;
    }
}
