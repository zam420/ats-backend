package com.deepconnect.ats.tenant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

@Getter 
public enum TenantType {
    PUBLIC(1L),     //for candidates
    OWNER(2L),
    OTHERS(3L);      //for ATS owning entity

    @JsonValue
    private final Long value;

    TenantType(Long value) {
        this.value = value;
    }

    @JsonCreator
    public static TenantType fromValue(Long value) {
        if (value == null) return null;
        for (TenantType s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}