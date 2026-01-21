package com.telemetryai.backend.apikey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public final class ApiKeyUtil {
    private static final SecureRandom RANDOM = new SecureRandom();

    private ApiKeyUtil() {
    }

    public static String generateKey() {
        byte[] raw = new byte[32];
        RANDOM.nextBytes(raw);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
    }

    public static String hashKey(String apiKey, byte[] salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            digest.update(apiKey.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest();
            String saltEncoded = Base64.getEncoder().encodeToString(salt);
            String hashEncoded = Base64.getEncoder().encodeToString(hash);
            return saltEncoded + ":" + hashEncoded;
        } catch (Exception e) {
            throw new IllegalStateException("Hashing failed", e);
        }
    }

    public static byte[] newSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static String last4(String apiKey) {
        if (apiKey == null || apiKey.length() <= 4) {
            return apiKey;
        }
        return apiKey.substring(apiKey.length() - 4);
    }

    public static String maskedKey(String last4) {
        if (last4 == null || last4.isBlank()) {
            return "****";
        }
        return "****" + last4;
    }
}
