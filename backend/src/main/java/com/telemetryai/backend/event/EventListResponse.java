package com.telemetryai.backend.event;

import java.util.List;

public class EventListResponse {
    private final List<EventItemResponse> items;

    public EventListResponse(List<EventItemResponse> items) {
        this.items = items;
    }

    public List<EventItemResponse> getItems() {
        return items;
    }
}
