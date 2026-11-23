package com.deepconnect.ats.jobapplication.enums;

import com.deepconnect.ats.common.enums.LocalizedBaseExtendedEnum;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum JobApplicationCloseType implements LocalizedBaseExtendedEnum {

    REJECTED(0, "rejected"),
    OFFER_CANCELLED(1, "offerCancelled"),
    NOT_JOINED(2, "notJoined");

    @JsonValue
    private final Integer id;

    private final String key;
    private final String code;

    private static final String enumsKeyPrefix = "enums.jobApplicationCloseType.";

    JobApplicationCloseType(Integer id, String key) {
        this.key = enumsKeyPrefix + key;
        this.id = id;
        this.code = this.name();
    }

    
}