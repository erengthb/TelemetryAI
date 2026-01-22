package com.telemetryai.backend.dashboard;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class DashboardRangeParser {
    private DashboardRangeParser() {
    }

    public static Range parse(String range, int defaultDays) {
        int days = defaultDays;
        if (range != null && !range.isBlank()) {
            String trimmed = range.trim().toLowerCase();
            if (trimmed.endsWith("d")) {
                days = safeParse(trimmed.substring(0, trimmed.length() - 1), defaultDays);
            } else {
                days = safeParse(trimmed, defaultDays);
            }
        }

        OffsetDateTime end = OffsetDateTime.now(ZoneOffset.UTC);
        OffsetDateTime start = end.minusDays(days);
        return new Range(start, end);
    }

    private static int safeParse(String value, int fallback) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    public record Range(OffsetDateTime from, OffsetDateTime to) {
    }
}
