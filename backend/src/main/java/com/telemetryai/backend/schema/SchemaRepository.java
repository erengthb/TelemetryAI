package com.telemetryai.backend.schema;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SchemaRepository extends JpaRepository<SchemaEntity, UUID> {
    Optional<SchemaEntity> findTopByProjectIdOrderByVersionDesc(UUID projectId);
}
