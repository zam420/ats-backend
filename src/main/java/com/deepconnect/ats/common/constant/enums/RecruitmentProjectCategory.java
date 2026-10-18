package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum RecruitmentProjectCategory implements BaseEnum {
    CAMPUS(0, "Campus"),
    SOCIAL(1, "Social"),
    INTERN(2, "Intern");

    @JsonValue
    private final Integer value;
    private final String name;

    RecruitmentProjectCategory(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static RecruitmentProjectCategory fromValue(Integer value) {
        if (value == null) return null;
        for (RecruitmentProjectCategory c : values()) {
            if (c.value.equals(value)) return c;
        }
        return null;
    }
}
