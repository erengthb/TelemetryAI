package com.telemetryai.backend.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuarantineEventRepository extends JpaRepository<QuarantineEventEntity, Long> {
}
