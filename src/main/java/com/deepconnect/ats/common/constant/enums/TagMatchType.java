package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum TagMatchType implements BaseEnum {
    FULLY_MATCH(0, "Fully Match"),
    PARTIAL_MATCH(1, "Partial Match");

    @JsonValue
    private final Integer value;
    private final String name;

    TagMatchType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static TagMatchType fromValue(Integer value) {
        if (value == null) return null;
        for (TagMatchType t : values()) {
            if (t.value.equals(value)) return t;
        }
        return null;
    }
}
