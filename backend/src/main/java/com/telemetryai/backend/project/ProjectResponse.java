package com.telemetryai.backend.project;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ProjectResponse {
    private final UUID id;
    private final UUID orgId;
    private final String name;
    private final OffsetDateTime createdAt;

    public ProjectResponse(UUID id, UUID orgId, String name, OffsetDateTime createdAt) {
        this.id = id;
        this.orgId = orgId;
        this.name = name;
        this.createdAt = createdAt;
    }

    public static ProjectResponse from(ProjectEntity project) {
        return new ProjectResponse(
            project.getId(),
            project.getOrgId(),
            project.getName(),
            project.getCreatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public String getName() {
        return name;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
