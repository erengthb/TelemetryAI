package com.telemetryai.backend.quarantine;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemetryai.backend.event.QuarantineEventEntity;
import com.telemetryai.backend.event.QuarantineEventRepository;
import com.telemetryai.backend.project.ProjectEntity;
import com.telemetryai.backend.project.ProjectRepository;
import com.telemetryai.backend.user.OrgMemberRepository;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class QuarantineService {
    private static final List<String> ALLOWED_ENVS = List.of("dev", "stage", "prod");
    private static final int DEFAULT_RANGE_DAYS = 7;
    private static final int DEFAULT_LIMIT = 100;

    private final QuarantineEventRepository quarantineEventRepository;
    private final ProjectRepository projectRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final ObjectMapper objectMapper;

    public QuarantineService(
            QuarantineEventRepository quarantineEventRepository,
            ProjectRepository projectRepository,
            OrgMemberRepository orgMemberRepository,
            ObjectMapper objectMapper
    ) {
        this.quarantineEventRepository = quarantineEventRepository;
        this.projectRepository = projectRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.objectMapper = objectMapper;
    }

    public QuarantineListResponse list(
            UserEntity user,
            UUID projectId,
            String envName,
            String range,
            String eventName,
            String reason,
            Integer limit,
            Integer offset
    ) {
        requireMember(user, projectId);
        validateEnv(envName);

        RangeParser.Range window = RangeParser.parse(range, DEFAULT_RANGE_DAYS);
        int safeLimit = limit == null || limit <= 0 ? DEFAULT_LIMIT : Math.min(limit, 500);
        int safeOffset = offset == null || offset < 0 ? 0 : offset;

        List<QuarantineEventEntity> results = quarantineEventRepository.search(
            projectId,
            envName,
            window.from(),
            window.to(),
            eventName,
            reason,
            safeLimit,
            safeOffset
        );

        List<QuarantineItemResponse> items = results.stream()
            .map(entity -> toResponse(entity, false))
            .toList();

        return new QuarantineListResponse(items);
    }

    public QuarantineItemResponse get(UserEntity user, UUID projectId, Long id) {
        requireMember(user, projectId);
        QuarantineEventEntity entity = quarantineEventRepository.findByIdAndProjectId(id, projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Quarantine event not found"));
        return toResponse(entity, true);
    }

    private QuarantineItemResponse toResponse(QuarantineEventEntity entity, boolean includeRaw) {
        List<String> reasons = parseReasons(entity.getReasons());
        JsonNode raw = includeRaw ? parseJson(entity.getRawEvent()) : null;
        return new QuarantineItemResponse(
            entity.getId(),
            entity.getEnvName(),
            entity.getEventId() == null ? null : entity.getEventId().toString(),
            entity.getEventName(),
            entity.getReceivedAt(),
            reasons,
            entity.getSchemaVersionAtTime(),
            entity.getClientSdk(),
            entity.getClientSdkVersion(),
            entity.getEngine(),
            entity.getEngineVersion(),
            entity.getBuildVersion(),
            entity.getPlatform(),
            raw
        );
    }

    private List<String> parseReasons(String reasonsJson) {
        if (reasonsJson == null || reasonsJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(reasonsJson, objectMapper.getTypeFactory().constructCollectionType(List.class, String.class));
        } catch (Exception e) {
            return List.of();
        }
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
