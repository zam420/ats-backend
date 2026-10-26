package com.deepconnect.ats.common.enums;

public enum PermissionType {

    INTERVIEW_PLAN_TEMPLATE_ADMIN("itma"),
    INTERVIEW_PLAN_TEMPLATE_CREATE("iptc"),
    INTERVIEW_PLAN_TEMPLATE_EDIT("ipte"),
    INTERVIEW_PLAN_TEMPLATE_SEARCH("ipts");

    private final String code;

    PermissionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
