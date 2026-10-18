package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum InterviewCandidateStatus implements BaseEnum {
    CANCELLED(-1, "Cancelled"),
    INTERVIEW_SENT(0, "Interview sent"),
    PASS(1, "Pass"),
    KEEP_IN_VIEW(2, "Keep in view"),
    NOT_PASS(3, "Not pass");

    @JsonValue
    private final Integer value;
    private final String name;

    InterviewCandidateStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static InterviewCandidateStatus fromValue(Integer value) {
        if (value == null) return null;
        for (InterviewCandidateStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
