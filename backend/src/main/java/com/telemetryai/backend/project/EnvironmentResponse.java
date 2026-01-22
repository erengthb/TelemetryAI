package com.telemetryai.backend.project;

import java.util.UUID;

public class EnvironmentResponse {
    private final UUID id;
    private final UUID projectId;
    private final String envName;

    public EnvironmentResponse(UUID id, UUID projectId, String envName) {
        this.id = id;
        this.projectId = projectId;
        this.envName = envName;
    }

    public static EnvironmentResponse from(EnvironmentEntity environment) {
        return new EnvironmentResponse(
            environment.getId(),
            environment.getProjectId(),
            environment.getEnvName()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getEnvName() {
        return envName;
    }
}
