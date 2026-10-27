package com.deepconnect.ats.common.constant.enums;

import lombok.Getter;

@Getter
public enum InterviewRoundType {
    HR_ROUND(1, "HR", "HR evaluation"),
    FUNCTIONAL_ROUND(2, "Functional", "To evaluate functional skills"),
    C_LEVEL_ROUND(3, "Executive", "Evaluation by executive level"),
    CUSTOM(4, "Custom", "Custom round");

    private final int value;
    private final String displayName;
    private final String description;

    InterviewRoundType(int value, String displayName, String description) {
        this.value = value;
        this.displayName = displayName;
        this.description = description;
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
