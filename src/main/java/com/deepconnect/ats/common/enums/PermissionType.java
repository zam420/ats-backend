package com.deepconnect.ats.common.enums;

public enum PermissionType {

    INTERVIEW_PLAN_TEMPLATE_SYSTEM_ADMIN("iptsa"),
    INTERVIEW_PLAN_TEMPLATE_ADMIN("ipta"),
    INTERVIEW_PLAN_TEMPLATE_CREATE("iptc"),
    INTERVIEW_PLAN_TEMPLATE_EDIT("ipte"),
    INTERVIEW_PLAN_TEMPLATE_SEARCH("ipts"),

    RECRUITMENT_PIPELINE_TEMPLATE_SYSTEM_ADMIN("rptsa"),
    RECRUITMENT_PIPELINE_TEMPLATE_ADMIN("rpta"),
    RECRUITMENT_PIPELINE_TEMPLATE_CREATE("rptc"),
    RECRUITMENT_PIPELINE_TEMPLATE_EDIT("rpte"),
    RECRUITMENT_PIPELINE_TEMPLATE_SEARCH("rpts"),

    SCREENING_QUESTION_TEMPLATE_ADMIN("sqta"),
    SCREENING_QUESTION_TEMPLATE_SYSTEM_ADMIN("sqtsa"),

    JOB_POSITION_ADMIN("jpa"),
    ;

    private final String code;

    PermissionType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
