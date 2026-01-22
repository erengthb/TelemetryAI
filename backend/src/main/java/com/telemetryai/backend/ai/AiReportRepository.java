package com.telemetryai.backend.ai;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AiReportRepository extends JpaRepository<AiReportEntity, Long> {
    List<AiReportEntity> findByProjectIdAndEnvNameAndReportTypeOrderByPeriodEndDesc(
            UUID projectId,
            String envName,
            String reportType
    );
}
