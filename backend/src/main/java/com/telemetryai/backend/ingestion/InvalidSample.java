package com.telemetryai.backend.ingestion;

import java.util.List;

public class InvalidSample {
    private final String eventId;
    private final String eventName;
    private final List<String> reasons;

    public InvalidSample(String eventId, String eventName, List<String> reasons) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.reasons = reasons;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public List<String> getReasons() {
        return reasons;
    }
}
