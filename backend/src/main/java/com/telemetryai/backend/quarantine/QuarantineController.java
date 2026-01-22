package com.telemetryai.backend.quarantine;

import com.telemetryai.backend.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/projects/{projectId}/quarantine")
public class QuarantineController {
    private final QuarantineService quarantineService;

    public QuarantineController(QuarantineService quarantineService) {
        this.quarantineService = quarantineService;
    }

    @GetMapping
    public QuarantineListResponse list(
            @PathVariable UUID projectId,
            @RequestParam("env") String envName,
            @RequestParam(value = "range", required = false) String range,
            @RequestParam(value = "eventName", required = false) String eventName,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return quarantineService.list(user, projectId, envName, range, eventName, reason, limit, offset);
    }

    @GetMapping("/{quarantineId}")
    public QuarantineItemResponse get(
            @PathVariable UUID projectId,
            @PathVariable Long quarantineId,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return quarantineService.get(user, projectId, quarantineId);
    }
}
