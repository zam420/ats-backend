package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum PoliticalStatus implements BaseEnum {
    CCP_MEMBER(0, "CCP Member"),
    PROBATIONARY_PARTY_MEMBER(1, "Probationary Party Member"),
    CYL_MEMBER(2, "CYL Member"),
    DEMOCRATIC_PARTIES(3, "Democratic Parties"),
    MASSES(4, "Masses");

    @JsonValue
    private final Integer value;
    private final String name;

    PoliticalStatus(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static PoliticalStatus fromValue(Integer value) {
        if (value == null) return null;
        for (PoliticalStatus s : values()) {
            if (s.value.equals(value)) return s;
        }
        return null;
    }
}
