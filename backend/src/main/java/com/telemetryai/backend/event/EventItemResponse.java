package com.telemetryai.backend.event;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;

public class EventItemResponse {
    private final Long id;
    private final String envName;
    private final String eventId;
    private final String eventName;
    private final String eventDescription;
    private final OffsetDateTime tsClient;
    private final OffsetDateTime tsServer;
    private final String playerId;
    private final String sessionId;
    private final String buildVersion;
    private final String platform;
    private final JsonNode device;
    private final JsonNode properties;

    public EventItemResponse(
            Long id,
            String envName,
            String eventId,
            String eventName,
            String eventDescription,
            OffsetDateTime tsClient,
            OffsetDateTime tsServer,
            String playerId,
            String sessionId,
            String buildVersion,
            String platform,
            JsonNode device,
            JsonNode properties
    ) {
        this.id = id;
        this.envName = envName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.tsClient = tsClient;
        this.tsServer = tsServer;
        this.playerId = playerId;
        this.sessionId = sessionId;
        this.buildVersion = buildVersion;
        this.platform = platform;
        this.device = device;
        this.properties = properties;
    }

    public Long getId() {
        return id;
    }

    public String getEnvName() {
        return envName;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public OffsetDateTime getTsClient() {
        return tsClient;
    }

    public OffsetDateTime getTsServer() {
        return tsServer;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public JsonNode getDevice() {
        return device;
    }

    public JsonNode getProperties() {
        return properties;
    }
}
