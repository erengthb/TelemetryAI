package com.telemetryai.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/projects/{projectId}/reports")
public class AiReportController {
    private final AiReportQueryService queryService;

    public AiReportController(AiReportQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/daily")
    public List<JsonNode> listDaily(
            @PathVariable UUID projectId,
            @RequestParam(value = "env", defaultValue = "prod") String envName,
            @RequestParam(value = "range", defaultValue = "30d") String range,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return queryService.list(user, projectId, envName, "daily");
    }

    @GetMapping("/weekly")
    public List<JsonNode> listWeekly(
            @PathVariable UUID projectId,
            @RequestParam(value = "env", defaultValue = "prod") String envName,
            @RequestParam(value = "range", defaultValue = "12w") String range,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return queryService.list(user, projectId, envName, "weekly");
    }
}
