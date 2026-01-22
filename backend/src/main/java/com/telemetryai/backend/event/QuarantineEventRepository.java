package com.telemetryai.backend.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuarantineEventRepository extends JpaRepository<QuarantineEventEntity, Long> {
    Optional<QuarantineEventEntity> findByIdAndProjectId(Long id, UUID projectId);

    @Query(value = """
        select * from quarantine_events
        where project_id = :projectId
          and env_name = :envName
          and received_at between :from and :to
          and (:eventName is null or event_name = :eventName)
          and (:reason is null or reasons ? :reason)
        order by received_at desc
        limit :limit offset :offset
        """, nativeQuery = true)
    List<QuarantineEventEntity> search(
            @Param("projectId") UUID projectId,
            @Param("envName") String envName,
            @Param("from") OffsetDateTime from,
            @Param("to") OffsetDateTime to,
            @Param("eventName") String eventName,
            @Param("reason") String reason,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}
