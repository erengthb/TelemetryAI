package com.telemetryai.backend.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemetryai.backend.apikey.ApiKeyEntity;
import com.telemetryai.backend.apikey.ApiKeyRepository;
import com.telemetryai.backend.apikey.ApiKeyUtil;
import com.telemetryai.backend.event.EventEntity;
import com.telemetryai.backend.event.EventRepository;
import com.telemetryai.backend.event.QuarantineEventEntity;
import com.telemetryai.backend.event.QuarantineEventRepository;
import com.telemetryai.backend.project.ProjectEntity;
import com.telemetryai.backend.project.ProjectRepository;
import com.telemetryai.backend.schema.SchemaEntity;
import com.telemetryai.backend.schema.SchemaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class IngestionService {
    private static final int MAX_BATCH_SIZE = 200;
    private static final int MAX_BODY_BYTES = 2_000_000;
    private static final int MAX_PROPERTIES_BYTES = 32_000;
    private static final int MAX_STRING_LENGTH = 1024;
    private static final int INVALID_SAMPLE_LIMIT = 5;

    private static final int RATE_LIMIT_RPS = 3;
    private static final int RATE_LIMIT_BURST = 5;

    private final ApiKeyRepository apiKeyRepository;
    private final ProjectRepository projectRepository;
    private final SchemaRepository schemaRepository;
    private final EventRepository eventRepository;
    private final QuarantineEventRepository quarantineEventRepository;
    private final ObjectMapper objectMapper;
    private final RateLimiterService rateLimiter;

    public IngestionService(
            ApiKeyRepository apiKeyRepository,
            ProjectRepository projectRepository,
            SchemaRepository schemaRepository,
            EventRepository eventRepository,
            QuarantineEventRepository quarantineEventRepository,
            ObjectMapper objectMapper
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.projectRepository = projectRepository;
        this.schemaRepository = schemaRepository;
        this.eventRepository = eventRepository;
        this.quarantineEventRepository = quarantineEventRepository;
        this.objectMapper = objectMapper;
        this.rateLimiter = new RateLimiterService(RATE_LIMIT_RPS, RATE_LIMIT_BURST);
    }

    @Transactional
    public BatchResponse ingest(String apiKey, BatchRequest request) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing API key");
        }

        if (request == null || request.getEvents() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payload");
        }

        int received = request.getEvents().size();
        if (received > MAX_BATCH_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Batch limit exceeded");
        }

        try {
            byte[] bodyBytes = objectMapper.writeValueAsBytes(request);
            if (bodyBytes.length > MAX_BODY_BYTES) {
                throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Payload too large");
            }
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payload parse failed");
        }

        ApiKeyEntity apiKeyEntity = resolveApiKey(apiKey);
        if (!rateLimiter.tryConsume(apiKeyEntity.getId().toString())) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
        ProjectEntity project = projectRepository.findById(apiKeyEntity.getProjectId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Project not found"));

        SchemaEntity schemaEntity = schemaRepository.findTopByProjectIdOrderByVersionDesc(project.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Schema not found"));

        SchemaIndex schemaIndex = SchemaIndex.from(parseSchema(schemaEntity.getSchemaJson()));

        List<UUID> eventIds = new ArrayList<>();
        for (EventPayload event : request.getEvents()) {
            UUID eventId = parseUuid(event == null ? null : event.getEventId());
            if (eventId != null) {
                eventIds.add(eventId);
            }
        }
        Set<UUID> duplicates = new HashSet<>();
        if (!eventIds.isEmpty()) {
            duplicates.addAll(eventRepository.findExistingEventIds(project.getId(), apiKeyEntity.getEnvName(), eventIds));
        }

        List<EventEntity> validEvents = new ArrayList<>();
        List<QuarantineEventEntity> invalidEvents = new ArrayList<>();
        List<InvalidSample> invalidSamples = new ArrayList<>();

        int accepted = 0;
        int invalid = 0;
        int duplicate = 0;

        for (EventPayload payload : request.getEvents()) {
            ValidationResult result = validateEvent(payload, request.getClient(), schemaIndex);
            UUID eventId = result.eventId;

            if (eventId != null && duplicates.contains(eventId)) {
                duplicate++;
                continue;
            }

            if (!result.reasons.isEmpty()) {
                invalid++;
                invalidEvents.add(buildQuarantine(payload, request.getClient(), project, apiKeyEntity.getEnvName(), schemaIndex.getVersion(), result.reasons));
                if (invalidSamples.size() < INVALID_SAMPLE_LIMIT) {
                    invalidSamples.add(new InvalidSample(
                        payload == null ? null : payload.getEventId(),
                        payload == null ? null : payload.getEventName(),
                        result.reasons
                    ));
                }
                continue;
            }

            EventEntity entity = buildEvent(payload, request.getClient(), project, apiKeyEntity.getEnvName(), schemaIndex);
            validEvents.add(entity);
            accepted++;
        }

        if (!validEvents.isEmpty()) {
            eventRepository.saveAll(validEvents);
        }
        if (!invalidEvents.isEmpty()) {
            quarantineEventRepository.saveAll(invalidEvents);
        }

        return new BatchResponse(received, accepted, invalid, duplicate, invalidSamples);
    }

    private ApiKeyEntity resolveApiKey(String apiKey) {
        String last4 = ApiKeyUtil.last4(apiKey);
        List<ApiKeyEntity> candidates = apiKeyRepository.findByKeyLast4AndRevokedAtIsNull(last4);
        for (ApiKeyEntity candidate : candidates) {
            if (ApiKeyUtil.verifyKey(candidate.getKeyHash(), apiKey)) {
                return candidate;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API key");
    }

    private JsonNode parseSchema(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Schema parse failed");
        }
    }

    private ValidationResult validateEvent(EventPayload payload, ClientInfo client, SchemaIndex schemaIndex) {
        List<String> reasons = new ArrayList<>();
        UUID eventId = null;

        if (payload == null) {
            reasons.add(ReasonCodes.missingRequired("eventId"));
            return new ValidationResult(null, reasons);
        }

        if (payload.getEventId() == null || payload.getEventId().isBlank()) {
            reasons.add(ReasonCodes.missingRequired("eventId"));
        } else {
            eventId = parseUuid(payload.getEventId());
            if (eventId == null) {
                reasons.add(ReasonCodes.missingRequired("eventId"));
            }
        }

        if (payload.getTimestampClient() == null || payload.getTimestampClient() <= 0) {
            reasons.add(ReasonCodes.missingRequired("timestampClient"));
        }
        if (isBlank(payload.getEventName())) {
            reasons.add(ReasonCodes.missingRequired("eventName"));
        }
        if (isBlank(payload.getSessionId())) {
            reasons.add(ReasonCodes.missingRequired("sessionId"));
        }
        if (isBlank(payload.getPlayerId())) {
            reasons.add(ReasonCodes.missingRequired("playerId"));
        }

        checkStringLength("eventName", payload.getEventName(), reasons);
        checkStringLength("sessionId", payload.getSessionId(), reasons);
        checkStringLength("playerId", payload.getPlayerId(), reasons);
        checkStringLength("buildVersion", payload.getBuildVersion(), reasons);
        checkStringLength("platform", payload.getPlatform(), reasons);

        SchemaIndex.EventSchema eventSchema = schemaIndex.getEvent(payload.getEventName());
        if (eventSchema == null) {
            reasons.add(ReasonCodes.EVENT_NOT_IN_SCHEMA);
            return new ValidationResult(eventId, reasons);
        }

        JsonNode properties = payload.getProperties();
        if (properties == null || properties.isNull()) {
            properties = objectMapper.createObjectNode();
        } else if (!properties.isObject()) {
            reasons.add(ReasonCodes.typeMismatch("properties"));
            return new ValidationResult(eventId, reasons);
        }

        try {
            byte[] propertiesBytes = objectMapper.writeValueAsBytes(properties);
            if (propertiesBytes.length > MAX_PROPERTIES_BYTES) {
                reasons.add(ReasonCodes.PAYLOAD_TOO_LARGE);
            }
        } catch (Exception e) {
            reasons.add(ReasonCodes.PAYLOAD_TOO_LARGE);
        }

        var propertyFields = properties.fields();
        var payloadProps = new java.util.HashMap<String, JsonNode>();
        while (propertyFields.hasNext()) {
            var entry = propertyFields.next();
            payloadProps.put(entry.getKey().toLowerCase(), entry.getValue());
        }

        for (SchemaIndex.PropertySchema propertySchema : eventSchema.getProperties().values()) {
            String key = propertySchema.getKey();
            JsonNode value = payloadProps.get(key.toLowerCase());
            if (value == null || value.isNull()) {
                if (propertySchema.isRequired()) {
                    reasons.add(ReasonCodes.missingRequired(key));
                }
                continue;
            }

            if (!typeMatches(propertySchema.getType(), value)) {
                reasons.add(ReasonCodes.typeMismatch(key));
                continue;
            }

            if (!propertySchema.getAllowed().isEmpty()) {
                String valueText = value.asText();
                if (!propertySchema.getAllowed().contains(valueText)) {
                    reasons.add(ReasonCodes.allowedViolation(key));
                }
            }

            if (value.isTextual() && value.textValue().length() > MAX_STRING_LENGTH) {
                reasons.add(ReasonCodes.fieldTooLong(key));
            }
        }

        var originalFields = properties.fields();
        while (originalFields.hasNext()) {
            var entry = originalFields.next();
            String key = entry.getKey();
            if (!eventSchema.getProperties().containsKey(key.toLowerCase())) {
                reasons.add(ReasonCodes.typeMismatch(key));
            }
        }

        if (PiiDetector.containsPii(properties)) {
            reasons.add(ReasonCodes.PII_DETECTED);
        }

        return new ValidationResult(eventId, reasons);
    }

    private EventEntity buildEvent(
            EventPayload payload,
            ClientInfo client,
            ProjectEntity project,
            String envName,
            SchemaIndex schemaIndex
    ) {
        EventEntity entity = new EventEntity();
        entity.setOrgId(project.getOrgId());
        entity.setProjectId(project.getId());
        entity.setEnvName(envName);
        entity.setEventId(UUID.fromString(payload.getEventId()));
        entity.setEventName(payload.getEventName());

        SchemaIndex.EventSchema schema = schemaIndex.getEvent(payload.getEventName());
        if (schema != null) {
            entity.setEventDescription(schema.getDescription());
        }

        OffsetDateTime tsClient = OffsetDateTime.ofInstant(
            Instant.ofEpochMilli(payload.getTimestampClient()),
            ZoneOffset.UTC
        );
        entity.setTsClient(tsClient);
        entity.setTsServer(OffsetDateTime.now(ZoneOffset.UTC));
        entity.setPlayerId(payload.getPlayerId());
        entity.setSessionId(payload.getSessionId());

        String buildVersion = payload.getBuildVersion();
        String platform = payload.getPlatform();
        if (client != null) {
            if (buildVersion == null || buildVersion.isBlank()) {
                buildVersion = client.getBuildVersion();
            }
            if (platform == null || platform.isBlank()) {
                platform = client.getPlatform();
            }
        }
        entity.setBuildVersion(buildVersion);
        entity.setPlatform(platform);

        try {
            String deviceJson = payload.getDevice() == null ? null : objectMapper.writeValueAsString(payload.getDevice());
            String propsJson = payload.getProperties() == null ? "{}" : objectMapper.writeValueAsString(payload.getProperties());
            entity.setDevice(deviceJson);
            entity.setProperties(propsJson);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid JSON payload");
        }

        return entity;
    }

    private QuarantineEventEntity buildQuarantine(
            EventPayload payload,
            ClientInfo client,
            ProjectEntity project,
            String envName,
            int schemaVersion,
            List<String> reasons
    ) {
        QuarantineEventEntity entity = new QuarantineEventEntity();
        entity.setOrgId(project.getOrgId());
        entity.setProjectId(project.getId());
        entity.setEnvName(envName);
        entity.setReceivedAt(OffsetDateTime.now(ZoneOffset.UTC));
        entity.setSchemaVersionAtTime(schemaVersion);

        if (payload != null) {
            entity.setEventName(payload.getEventName());
            entity.setEventId(parseUuid(payload.getEventId()));
        }

        if (client != null) {
            entity.setClientSdk(client.getSdk());
            entity.setClientSdkVersion(client.getSdkVersion());
            entity.setEngine(client.getEngine());
            entity.setEngineVersion(client.getEngineVersion());
            entity.setBuildVersion(client.getBuildVersion());
            entity.setPlatform(client.getPlatform());
        }

        try {
            JsonNode raw = objectMapper.valueToTree(payload);
            entity.setRawEvent(objectMapper.writeValueAsString(raw));
            entity.setReasons(objectMapper.writeValueAsString(reasons));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid payload");
        }

        return entity;
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void checkStringLength(String field, String value, List<String> reasons) {
        if (value != null && value.length() > MAX_STRING_LENGTH) {
            reasons.add(ReasonCodes.fieldTooLong(field));
        }
    }

    private boolean typeMatches(String type, JsonNode value) {
        if (type == null) {
            return true;
        }
        return switch (type) {
            case "string" -> value.isTextual();
            case "number" -> value.isNumber();
            case "boolean" -> value.isBoolean();
            case "object" -> value.isObject();
            case "array" -> value.isArray();
            default -> false;
        };
    }

    private static final class ValidationResult {
        private final UUID eventId;
        private final List<String> reasons;

        private ValidationResult(UUID eventId, List<String> reasons) {
            this.eventId = eventId;
            this.reasons = reasons;
        }
    }
}
