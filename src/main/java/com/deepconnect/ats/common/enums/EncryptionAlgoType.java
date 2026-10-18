package com.deepconnect.ats.common.enums;

public enum EncryptionAlgoType {

    AES("AES"),
    DES("DES"),
    RSA("RSA");

    private final String name;

    EncryptionAlgoType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
