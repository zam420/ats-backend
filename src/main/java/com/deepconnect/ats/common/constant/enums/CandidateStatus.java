package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CandidateStatus implements BaseEnum {
    ELIMINATE(-1, "Eliminate"),
    DEFAULT(0, "Default"),
    INTERVIEW(1, "Interview"),
    OFFER(2, "Offer"),
    ONBOARDING(3, "Onboarding");

    @JsonValue
    private final Integer value;
    private final String name;

    CandidateStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static CandidateStatus fromValue(Integer value) {
        if (value == null) return null;
        for (CandidateStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
