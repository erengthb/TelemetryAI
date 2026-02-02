package com.telemetryai.backend.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.telemetryai.backend.common.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/schema")
public class SchemaPublicController {
    private static final int SCHEMA_RPS = 2;
    private static final int SCHEMA_BURST = 5;

    private final SchemaPublicService schemaPublicService;
    private final RateLimiterService rateLimiter = new RateLimiterService(SCHEMA_RPS, SCHEMA_BURST);

    public SchemaPublicController(SchemaPublicService schemaPublicService) {
        this.schemaPublicService = schemaPublicService;
    }

    @GetMapping("/current")
    public JsonNode currentSchema(
            @RequestHeader("X-Api-Key") String apiKey,
            HttpServletRequest request
    ) {
        if (!rateLimiter.tryConsume(resolveRateKey(request))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }
        return schemaPublicService.getCurrentSchema(apiKey);
    }

    private String resolveRateKey(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
