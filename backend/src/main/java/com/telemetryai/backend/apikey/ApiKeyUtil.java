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

    public static boolean verifyKey(String storedHash, String apiKey) {
        if (storedHash == null || apiKey == null) {
            return false;
        }
        String[] parts = storedHash.split(":");
        if (parts.length != 2) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[1]);
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            digest.update(apiKey.getBytes(StandardCharsets.UTF_8));
            byte[] actualHash = digest.digest();
            return constantTimeEquals(expectedHash, actualHash);
        } catch (Exception e) {
            return false;
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

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null || a.length != b.length) {
            return false;
        }
        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }
}
