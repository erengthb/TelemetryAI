package com.telemetryai.backend.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.telemetryai.backend.project.ProjectEntity;
import com.telemetryai.backend.project.ProjectRepository;
import com.telemetryai.backend.user.OrgMemberEntity;
import com.telemetryai.backend.user.OrgMemberRepository;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class SchemaService {
    private static final int MAX_EVENTS = 200;
    private static final int MAX_PROPERTIES = 50;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    private final SchemaRepository schemaRepository;
    private final ProjectRepository projectRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final ObjectMapper objectMapper;

    public SchemaService(
            SchemaRepository schemaRepository,
            ProjectRepository projectRepository,
            OrgMemberRepository orgMemberRepository,
            ObjectMapper objectMapper
    ) {
        this.schemaRepository = schemaRepository;
        this.projectRepository = projectRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public JsonNode importSchema(UserEntity user, UUID projectId, JsonNode inputSchema) {
        requireAdmin(user, projectId);

        List<String> errors = SchemaValidator.validate(inputSchema, MAX_EVENTS, MAX_PROPERTIES, MAX_DESCRIPTION_LENGTH);
        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.join("; ", errors));
        }

        int nextVersion = schemaRepository.findTopByProjectIdOrderByVersionDesc(projectId)
            .map(schema -> schema.getVersion() + 1)
            .orElse(1);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        UUID schemaId = UUID.randomUUID();

        ObjectNode normalized = objectMapper.createObjectNode();
        normalized.put("schemaId", schemaId.toString());
        normalized.put("schemaVersion", nextVersion);
        normalized.put("projectId", projectId.toString());
        normalized.put("updatedAt", now.toString());
        normalized.set("events", inputSchema.get("events").deepCopy());

        SchemaEntity entity = new SchemaEntity();
        entity.setId(schemaId);
        entity.setProjectId(projectId);
        entity.setVersion(nextVersion);
        entity.setCreatedAt(now);
        try {
            entity.setSchemaJson(objectMapper.writeValueAsString(normalized));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Schema serialize failed");
        }
        schemaRepository.save(entity);

        return normalized;
    }

    public JsonNode getCurrentSchema(UserEntity user, UUID projectId) {
        requireMember(user, projectId);
        SchemaEntity entity = schemaRepository.findTopByProjectIdOrderByVersionDesc(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schema not found"));

        return parseSchema(entity.getSchemaJson());
    }

    public JsonNode exportSchema(UserEntity user, UUID projectId) {
        return getCurrentSchema(user, projectId);
    }

    private JsonNode parseSchema(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Schema parse failed");
        }
    }

    private ProjectEntity requireMember(UserEntity user, UUID projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        orgMemberRepository.findByUserIdAndOrgId(user.getId(), project.getOrgId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));

        return project;
    }

    private ProjectEntity requireAdmin(UserEntity user, UUID projectId) {
        ProjectEntity project = requireMember(user, projectId);
        OrgMemberEntity membership = orgMemberRepository.findByUserIdAndOrgId(user.getId(), project.getOrgId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));

        if (!"admin".equalsIgnoreCase(membership.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }

        return project;
    }
}
