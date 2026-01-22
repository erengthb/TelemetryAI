package com.telemetryai.backend.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    @Query("select e.eventId from EventEntity e where e.projectId = :projectId and e.envName = :envName and e.eventId in :eventIds")
    List<UUID> findExistingEventIds(
            @Param("projectId") UUID projectId,
            @Param("envName") String envName,
            @Param("eventIds") List<UUID> eventIds
    );
}
