package com.telemetryai.backend.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/projects/{projectId}/schema")
public class SchemaController {
    private final SchemaService schemaService;

    public SchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @PostMapping("/import")
    public JsonNode importSchema(
            @PathVariable UUID projectId,
            @RequestBody JsonNode schema,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return schemaService.importSchema(user, projectId, schema);
    }

    @GetMapping("/current")
    public JsonNode currentSchema(
            @PathVariable UUID projectId,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return schemaService.getCurrentSchema(user, projectId);
    }

    @GetMapping("/export")
    public JsonNode exportSchema(
            @PathVariable UUID projectId,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return schemaService.exportSchema(user, projectId);
    }
}
