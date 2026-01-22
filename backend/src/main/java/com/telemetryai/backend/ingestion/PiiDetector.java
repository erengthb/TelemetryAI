package com.telemetryai.backend.ingestion;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class PiiDetector {
    private static final Set<String> PII_KEYS = Set.of(
        "email", "e-mail", "phone", "telephone", "gsm",
        "name", "surname", "fullname", "address", "location",
        "tc", "tckn", "identity", "passport"
    );

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", Pattern.CASE_INSENSITIVE);
    private static final Pattern PHONE_PATTERN =
        Pattern.compile("\\+?\\d{10,15}");

    private PiiDetector() {
    }

    public static boolean containsPii(JsonNode properties) {
        if (properties == null || !properties.isObject()) {
            return false;
        }

        var fields = properties.fields();
        while (fields.hasNext()) {
            var entry = fields.next();
            String key = entry.getKey().toLowerCase(Locale.ROOT);
            if (PII_KEYS.contains(key)) {
                return true;
            }
            JsonNode value = entry.getValue();
            if (value != null && value.isTextual()) {
                String text = value.textValue();
                if (EMAIL_PATTERN.matcher(text).find() || PHONE_PATTERN.matcher(text).find()) {
                    return true;
                }
            }
        }

        return false;
    }
}
