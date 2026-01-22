package com.telemetryai.backend.org;

import java.time.OffsetDateTime;
import java.util.UUID;

public class OrgResponse {
    private final UUID id;
    private final String name;
    private final String status;
    private final OffsetDateTime createdAt;

    public OrgResponse(UUID id, String name, String status, OffsetDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static OrgResponse from(OrgEntity org) {
        return new OrgResponse(org.getId(), org.getName(), org.getStatus(), org.getCreatedAt());
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
