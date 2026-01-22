package com.telemetryai.backend.apikey;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ApiKeyResponse {
    private final UUID id;
    private final String envName;
    private final String maskedKey;
    private final String status;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime revokedAt;

    public ApiKeyResponse(
            UUID id,
            String envName,
            String maskedKey,
            String status,
            OffsetDateTime createdAt,
            OffsetDateTime revokedAt
    ) {
        this.id = id;
        this.envName = envName;
        this.maskedKey = maskedKey;
        this.status = status;
        this.createdAt = createdAt;
        this.revokedAt = revokedAt;
    }

    public static ApiKeyResponse from(ApiKeyEntity entity) {
        String masked = ApiKeyUtil.maskedKey(entity.getKeyLast4());
        String status = entity.getRevokedAt() == null ? "active" : "revoked";
        return new ApiKeyResponse(
            entity.getId(),
            entity.getEnvName(),
            masked,
            status,
            entity.getCreatedAt(),
            entity.getRevokedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public String getEnvName() {
        return envName;
    }

    public String getMaskedKey() {
        return maskedKey;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getRevokedAt() {
        return revokedAt;
    }
}
