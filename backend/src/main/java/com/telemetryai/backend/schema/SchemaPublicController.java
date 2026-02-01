package com.telemetryai.backend.schema;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/schema")
public class SchemaPublicController {
    private final SchemaPublicService schemaPublicService;

    public SchemaPublicController(SchemaPublicService schemaPublicService) {
        this.schemaPublicService = schemaPublicService;
    }

    @GetMapping("/current")
    public JsonNode currentSchema(@RequestHeader("X-Api-Key") String apiKey) {
        return schemaPublicService.getCurrentSchema(apiKey);
    }
}
