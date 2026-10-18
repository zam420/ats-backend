package com.deepconnect.ats.common.constant.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Currency implements BaseEnum {
    CNY(0, "RMB/CNY"),
    USD(1, "USD"),
    EUR(2, "EUR"),
    INR(3, "INR"),
    TWD(4, "TWD"),
    SGD(5, "SGD"),
    BRL(6, "BRL"),
    CZK(7, "CZK");

    @JsonValue
    private final Integer value;
    private final String name;

    Currency(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    @JsonCreator
    public static Currency fromValue(Integer value) {
        if (value == null) return null;
        for (Currency c : values()) {
            if (c.value.equals(value)) return c;
        }
        return null;
    }
}
