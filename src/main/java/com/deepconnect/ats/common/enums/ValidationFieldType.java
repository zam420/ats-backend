package com.deepconnect.ats.common.enums;

import lombok.Getter;

@Getter
public enum ValidationFieldType {
    PRECISION("geolocation.precision"),
    LONGITUDE("geolocation.longitude"),
    LATITUDE("geolocation.latitude");

    private final String key;

    ValidationFieldType(String key) {
        this.key = key;
    }
}
