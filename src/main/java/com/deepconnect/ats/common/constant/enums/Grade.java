package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Grade implements BaseEnum {
    G_1(0, "1"),
    G_2(1, "2"),
    G_3(2, "3"),
    G_4(3, "4"),
    G_5(4, "5"),
    G_6(5, "6"),
    G_7(6, "7"),
    G_8(7, "8"),
    G_9(8, "9"),
    G_10(9, "10"),
    G_11(10, "11"),
    G_12(11, "12"),
    G_13(12, "13"),
    G_14(13, "14"),
    G_15(14, "15"),
    G_16(15, "16");

    @JsonValue
    private final Integer value;
    private final String name;

    Grade(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static Grade fromValue(Integer value) {
        if (value == null) return null;
        for (Grade g : values()) {
            if (g.value.equals(value)) return g;
        }
        return null;
    }
}
