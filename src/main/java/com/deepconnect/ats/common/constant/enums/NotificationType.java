package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum NotificationType implements BaseEnum {
    RESUME(0, "Resume"),
    INTERVIEW_TEAMS_SCHEDULE_RSP(1, "Interview Teams Schedule Response"),
    MAIL_SEND_RESULT(2, "Mail Send Result"),
    RESUME_COMMENT(3, "Resume Comment");

    @JsonValue
    private final Integer value;
    private final String name;

    NotificationType(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static NotificationType fromValue(Integer value) {
        if (value == null) return null;
        for (NotificationType n : values()) {
            if (n.value.equals(value)) return n;
        }
        return null;
    }
}
