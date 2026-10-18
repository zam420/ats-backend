package com.deepconnect.ats.common.enums;

public enum MacAlgoType {

    HMAC_SHA1("HmacSHA1"),
    HMAC_SHA256("HmacSHA256"),
    HMAC_SHA384("HmacSHA384"),
    HMAC_SHA512("HmacSHA512");

    private final String name;

    MacAlgoType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
}