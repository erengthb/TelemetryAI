package com.telemetryai.backend.ingestion;

public final class ReasonCodes {
    public static final String EVENT_NOT_IN_SCHEMA = "EVENT_NOT_IN_SCHEMA";
    public static final String PAYLOAD_TOO_LARGE = "PAYLOAD_TOO_LARGE";
    public static final String PII_DETECTED = "PII_DETECTED";

    private ReasonCodes() {
    }

    public static String missingRequired(String key) {
        return "MISSING_REQUIRED_PROPERTY:" + key;
    }

    public static String typeMismatch(String key) {
        return "TYPE_MISMATCH:" + key;
    }

    public static String allowedViolation(String key) {
        return "ALLOWED_VIOLATION:" + key;
    }

    public static String fieldTooLong(String field) {
        return "FIELD_TOO_LONG:" + field;
    }
}
