package com.deepconnect.ats.common.constant.enums;

import lombok.Getter;

@Getter
public enum InterviewRoundTypeEnum {
    HR_ROUND(1, "HR", "HR evaluation"),
    FUNCTIONAL_ROUND(2, "Functional", "To evaluate functional skills"),
    C_LEVEL_ROUND(3, "Executive", "Evaluation by executive level"),
    CUSTOM(4, "Custom", "Custom round");

    private final int value;
    private final String displayName;
    private final String description;

    InterviewRoundTypeEnum(int value, String displayName, String description) {
        this.value = value;
        this.displayName = displayName;
        this.description = description;
    }

    /** get_by_value: returns null when unknown. */
    public static InterviewRoundTypeEnum getByValue(Integer value) {
        if (value == null) return null;
        for (InterviewRoundTypeEnum t : values()) {
            if (t.value == value) return t;
        }
        return null;
    }

}
