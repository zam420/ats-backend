package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DirectSupervisorComment implements BaseEnum {
    RECOMMEND_FOR_RE_EMPLOYMENT(0, "Recommend for re-employment"),
    NOT_RECOMMEND_FOR_RE_EMPLOYMENT(1, "Not recommend for re-employment");

    @JsonValue
    private final Integer value;
    private final String name;

    DirectSupervisorComment(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static DirectSupervisorComment fromValue(Integer value) {
        if (value == null) return null;
        for (DirectSupervisorComment d : values()) {
            if (d.value.equals(value)) return d;
        }
        return null;
    }
}
