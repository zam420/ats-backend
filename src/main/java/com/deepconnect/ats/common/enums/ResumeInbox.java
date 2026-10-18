package com.deepconnect.ats.common.enums;

public enum ResumeInbox {
    CAMPUS("campus@espressif.com"),
    RECRUIT("recruit@espressif.com"),
    CAMPUS_IN("campus.in@espressif.com"),
    RECRUIT_IN("recruit.in@espressif.com"),
    RECRUIT_SGPR("recruit.sgpr@espressif.com"),
    RECRUIT_CZ("recruit.cz@espressif.com"),
    RECRUIT_BR("recruit.br@espressif.com"),
    RECRUIT_JP("recruit.jp@espressif.com");

    private final String value;
    ResumeInbox(String value) { this.value = value; }
    public String getValue() { return value; }

    public static ResumeInbox getByValue(String value) {
        for (ResumeInbox inbox : values()) if (inbox.value.equals(value)) return inbox;
        return null;
    }
}
