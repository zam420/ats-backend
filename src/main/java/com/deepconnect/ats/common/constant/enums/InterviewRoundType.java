package com.deepconnect.ats.common.constant.enums;

import lombok.Getter;

@Getter
public enum InterviewRoundType {
    HR_ROUND(2, "HR Interview"),
    FUNCTIONAL_ROUND(3, "Functional Interview"),
    C_LEVEL_ROUND(4, "Executive Interview"),
    CUSTOM(5, "Custom Interview");

    private final int value;
    private final String displayName;

    InterviewRoundType(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /** get_by_value: returns null when unknown. */
    public static InterviewRoundType getByValue(Integer value) {
        if (value == null) return null;
        for (InterviewRoundType t : values()) {
            if (t.value == value) return t;
        }
        return null;
    }

}
