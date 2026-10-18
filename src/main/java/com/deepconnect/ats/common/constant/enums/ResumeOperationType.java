package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ResumeOperationType implements BaseEnum {
    MOVE_TO(0, "Move To"),
    FORWARD(1, "Forward"),
    FORWARD_FEEDBACK(2, "Forward Feedback"),
    SCHEDULED_INTERVIEW(3, "Scheduled Interview"),
    INTERVIEW_END_TIME_REACHED(4, "Interview End Time Reached"),
    INTERVIEW_FEEDBACK(5, "Interview Feedback"),
    SENT_OFFER(6, "Sent Offer"),
    ONBOARDING_TIME_REACHED(7, "Onboarding Time Reached"),
    WRITTEN_TEST(8, "Written Test"),
    CHANGE_APPLIED_POSITION(9, "Change Applied Position"),
    SUBMIT_POSITION_APPLICATION(10, "Submit Position Application"),
    EXAM_FORWARD(11, "Exam Forward"),
    EXAM_FORWARD_FEEDBACK(12, "Exam Forward Feedback"),
    RESUME_UPDATE_FILE(13, "Resume Update File");

    @JsonValue
    private final Integer value;
    private final String name;

    ResumeOperationType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static ResumeOperationType fromValue(Integer value) {
        if (value == null) return null;
        for (ResumeOperationType o : values()) {
            if (o.value.equals(value)) return o;
        }
        return null;
    }
}
