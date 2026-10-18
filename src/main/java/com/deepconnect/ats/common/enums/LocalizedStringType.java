package com.deepconnect.ats.common.enums;

import lombok.Getter;

@Getter
public enum LocalizedStringType {
    ANONYMOUS_USER_DISPLAY_NAME("misc.anonymousUserDisplayName"),
    ;

    private final String key;

    LocalizedStringType(String key) {
        this.key = key;
    }
}
