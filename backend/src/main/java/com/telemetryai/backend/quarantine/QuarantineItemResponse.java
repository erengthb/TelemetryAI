package com.telemetryai.backend.quarantine;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.OffsetDateTime;
import java.util.List;

public class QuarantineItemResponse {
    private final Long id;
    private final String envName;
    private final String eventId;
    private final String eventName;
    private final OffsetDateTime receivedAt;
    private final List<String> reasons;
    private final Integer schemaVersion;
    private final String clientSdk;
    private final String clientSdkVersion;
    private final String engine;
    private final String engineVersion;
    private final String buildVersion;
    private final String platform;
    private final JsonNode rawEvent;

    public QuarantineItemResponse(
            Long id,
            String envName,
            String eventId,
            String eventName,
            OffsetDateTime receivedAt,
            List<String> reasons,
            Integer schemaVersion,
            String clientSdk,
            String clientSdkVersion,
            String engine,
            String engineVersion,
            String buildVersion,
            String platform,
            JsonNode rawEvent
    ) {
        this.id = id;
        this.envName = envName;
        this.eventId = eventId;
        this.eventName = eventName;
        this.receivedAt = receivedAt;
        this.reasons = reasons;
        this.schemaVersion = schemaVersion;
        this.clientSdk = clientSdk;
        this.clientSdkVersion = clientSdkVersion;
        this.engine = engine;
        this.engineVersion = engineVersion;
        this.buildVersion = buildVersion;
        this.platform = platform;
        this.rawEvent = rawEvent;
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

    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }

    public List<String> getReasons() {
        return reasons;
    }

    public Integer getSchemaVersion() {
        return schemaVersion;
    }

    public String getClientSdk() {
        return clientSdk;
    }

    public String getClientSdkVersion() {
        return clientSdkVersion;
    }

    public String getEngine() {
        return engine;
    }

    public String getEngineVersion() {
        return engineVersion;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public JsonNode getRawEvent() {
        return rawEvent;
    }
}
