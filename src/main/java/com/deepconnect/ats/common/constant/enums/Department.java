package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Department implements BaseEnum {
    IC_DESIGN(0, "IC Design"),
    SYSTEM_SOLUTION(1, "System Solution"),
    SOFTWARE(2, "Software"),
    HARDWARE(3, "Hardware"),
    PRODUCT(4, "Product"),
    QUALITY_ASSURANCE(5, "Quality Assurance"),
    TESTING(6, "Testing"),
    MARKETING(7, "Marketing"),
    SALES(8, "Sales"),
    FAE(9, "FAE"),
    FINANCE(10, "Finance"),
    HR(11, "HR"),
    IT(12, "IT"),
    LOGISTIC(13, "Logistic"),
    LEGAL(14, "Legal"),
    ADMINISTRATIVE(15, "Administrative");

    @JsonValue
    private final Integer value;
    private final String name;

    Department(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static Department fromValue(Integer value) {
        if (value == null) return null;
        for (Department d : values()) {
            if (d.value.equals(value)) return d;
        }
        return null;
    }
}
