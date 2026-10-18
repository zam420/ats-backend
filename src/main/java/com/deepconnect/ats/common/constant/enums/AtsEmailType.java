package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum AtsEmailType implements BaseEnum {
    FORWARD_NOTIFY_EMAIL(0, "Forward Notify Email"),
    FORWARD_FEEDBACK_EMAIL(1, "Forward Feedback Email"),
    CANDIDATE_INTERVIEW_EMAIL(2, "Candidate Interview Email"),
    TALENT_POOL_EMAIL(3, "Talent Pool Email"),
    CONTACT_CANDIDATE_EMAIL(4, "Contact Candidate Email"),
    EXAM_CREATE_TEST_EMAIL(5, "Exam Create Test Email"),
    EXAM_FORWARD_EMAIL(6, "Exam Forward Email"),
    TEAMS_INTERVIEW_SCHEDULE_EMAIL(7, "Teams Interview Schedule Email");

    @JsonValue
    private final Integer value;
    private final String name;

    AtsEmailType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static AtsEmailType fromValue(Integer value) {
        if (value == null) return null;
        for (AtsEmailType e : values()) {
            if (e.value.equals(value)) return e;
        }
        return null;
    }
}
