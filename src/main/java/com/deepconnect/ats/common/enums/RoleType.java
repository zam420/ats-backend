package com.deepconnect.ats.common.enums;

public enum RoleType {

    HR_ADMIN("hra"),
    RECRUITER("rec"),
    HIRING_MANAGER("hm"),
    INTERVIEWER("itv"),
    CANDIDATE("cdt");

    private final String code;

    RoleType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
