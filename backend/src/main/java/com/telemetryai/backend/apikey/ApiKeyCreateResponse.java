package com.telemetryai.backend.apikey;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ApiKeyCreateResponse {
    private final UUID id;
    private final String envName;
    private final String apiKey;
    private final String maskedKey;
    private final OffsetDateTime createdAt;

    public ApiKeyCreateResponse(UUID id, String envName, String apiKey, String maskedKey, OffsetDateTime createdAt) {
        this.id = id;
        this.envName = envName;
        this.apiKey = apiKey;
        this.maskedKey = maskedKey;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getEnvName() {
        return envName;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getMaskedKey() {
        return maskedKey;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
