package com.telemetryai.backend.ingestion;

import java.util.List;

public class BatchRequest {
    private ClientInfo client;
    private List<EventPayload> events;

    public ClientInfo getClient() {
        return client;
    }

    public void setClient(ClientInfo client) {
        this.client = client;
    }

    public List<EventPayload> getEvents() {
        return events;
    }

    public void setEvents(List<EventPayload> events) {
        this.events = events;
    }
}
