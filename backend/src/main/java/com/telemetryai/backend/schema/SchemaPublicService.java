package com.telemetryai.backend.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemetryai.backend.apikey.ApiKeyEntity;
import com.telemetryai.backend.apikey.ApiKeyRepository;
import com.telemetryai.backend.apikey.ApiKeyUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SchemaPublicService {
    private final ApiKeyRepository apiKeyRepository;
    private final SchemaRepository schemaRepository;
    private final ObjectMapper objectMapper;

    public SchemaPublicService(
            ApiKeyRepository apiKeyRepository,
            SchemaRepository schemaRepository,
            ObjectMapper objectMapper
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.schemaRepository = schemaRepository;
        this.objectMapper = objectMapper;
    }

    public JsonNode getCurrentSchema(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing API key");
        }

        ApiKeyEntity apiKeyEntity = resolveApiKey(apiKey);
        SchemaEntity entity = schemaRepository.findTopByProjectIdOrderByVersionDesc(apiKeyEntity.getProjectId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Schema not found"));

        return parseSchema(entity.getSchemaJson());
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
}
