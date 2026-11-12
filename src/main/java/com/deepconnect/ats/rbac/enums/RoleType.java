package com.deepconnect.ats.rbac.enums;

public enum RoleType {

    SYSTEM_ADMIN("sa"),
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
