package com.telemetryai.backend.ingestion;

import com.fasterxml.jackson.databind.JsonNode;

public class EventPayload {
    private String eventId;
    private Long timestampClient;
    private String eventName;
    private String sessionId;
    private String playerId;
    private String buildVersion;
    private String platform;
    private JsonNode device;
    private JsonNode properties;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public Long getTimestampClient() {
        return timestampClient;
    }

    public void setTimestampClient(Long timestampClient) {
        this.timestampClient = timestampClient;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public JsonNode getDevice() {
        return device;
    }

    public void setDevice(JsonNode device) {
        this.device = device;
    }

    public JsonNode getProperties() {
        return properties;
    }

    public void setProperties(JsonNode properties) {
        this.properties = properties;
    }
}
