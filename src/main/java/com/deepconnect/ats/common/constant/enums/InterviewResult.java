package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum InterviewResult implements BaseEnum {
    PASS(0, "Pass"),
    KEEP_IN_VIEW(1, "Keep in view"),
    NOT_PASS(2, "Not pass");

    @JsonValue
    private final Integer value;
    private final String name;

    InterviewResult(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public InterviewCandidateStatus triggerInterviewCandidateStatus() {
        return switch (this) {
            case PASS -> InterviewCandidateStatus.PASS;
            case KEEP_IN_VIEW -> InterviewCandidateStatus.KEEP_IN_VIEW;
            case NOT_PASS -> InterviewCandidateStatus.NOT_PASS;
        };
    }

    @JsonCreator
    public static InterviewResult fromValue(Integer value) {
        if (value == null) return null;
        for (InterviewResult r : values()) {
            if (r.value.equals(value)) return r;
        }
        return null;
    }
}
