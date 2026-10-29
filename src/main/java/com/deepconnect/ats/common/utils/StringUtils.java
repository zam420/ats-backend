package com.deepconnect.ats.common.utils;

public class StringUtils {
    
    public static String blankToNullAndTrim(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    public static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
