package com.deepconnect.ats.common.security;

import java.security.SecureRandom;

public class PublicIdGenerator {

    private static final int DEFAULT_SIZE = 12;
    private static final char[] ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int size) {
        char[] buffer = new char[size];
        for (int i = 0; i < size; i++) {
            buffer[i] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(buffer);
    }

    public static String generate() {
        return generate(DEFAULT_SIZE);
    }
}