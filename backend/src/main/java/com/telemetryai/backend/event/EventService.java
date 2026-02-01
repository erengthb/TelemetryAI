package com.telemetryai.backend.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemetryai.backend.project.ProjectEntity;
import com.telemetryai.backend.project.ProjectRepository;
import com.telemetryai.backend.quarantine.RangeParser;
import com.telemetryai.backend.user.OrgMemberRepository;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class EventService {
    private static final List<String> ALLOWED_ENVS = List.of("dev", "stage", "prod");
    private static final int DEFAULT_RANGE_DAYS = 7;
    private static final int DEFAULT_LIMIT = 100;

    private final EventRepository eventRepository;
    private final ProjectRepository projectRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final ObjectMapper objectMapper;

    public EventService(
            EventRepository eventRepository,
            ProjectRepository projectRepository,
            OrgMemberRepository orgMemberRepository,
            ObjectMapper objectMapper
    ) {
        this.eventRepository = eventRepository;
        this.projectRepository = projectRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.objectMapper = objectMapper;
    }

    public EventListResponse list(
            UserEntity user,
            UUID projectId,
            String envName,
            String range,
            Integer limit
    ) {
        requireMember(user, projectId);
        validateEnv(envName);

        RangeParser.Range window = RangeParser.parse(range, DEFAULT_RANGE_DAYS);
        int safeLimit = limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, 500);

        List<EventItemResponse> items = eventRepository
            .findRecent(projectId, envName, window.from(), window.to(), PageRequest.of(0, safeLimit))
            .stream()
            .map(this::toResponse)
            .toList();

        return new EventListResponse(items);
    }

    private EventItemResponse toResponse(EventEntity entity) {
        return new EventItemResponse(
            entity.getId(),
            entity.getEnvName(),
            entity.getEventId() == null ? null : entity.getEventId().toString(),
            entity.getEventName(),
            entity.getEventDescription(),
            entity.getTsClient(),
            entity.getTsServer(),
            entity.getPlayerId(),
            entity.getSessionId(),
            entity.getBuildVersion(),
            entity.getPlatform(),
            parseJson(entity.getDevice()),
            parseJson(entity.getProperties())
        );
    }

    private JsonNode parseJson(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return null;
        }
    }

    private ProjectEntity requireMember(UserEntity user, UUID projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        orgMemberRepository.findByUserIdAndOrgId(user.getId(), project.getOrgId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));

        return project;
    }

    private void validateEnv(String envName) {
        if (envName == null || !ALLOWED_ENVS.contains(envName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid env");
        }
    }
}
