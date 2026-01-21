package com.telemetryai.backend.apikey;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, UUID> {
    List<ApiKeyEntity> findByProjectId(UUID projectId);

    List<ApiKeyEntity> findByProjectIdAndEnvName(UUID projectId, String envName);

    List<ApiKeyEntity> findByKeyLast4AndRevokedAtIsNull(String keyLast4);
}
