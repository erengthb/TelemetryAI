package com.telemetryai.backend.event;

import com.telemetryai.backend.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/projects/{projectId}/events")
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public EventListResponse list(
            @PathVariable UUID projectId,
            @RequestParam String env,
            @RequestParam(required = false) String range,
            @RequestParam(required = false) Integer limit,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return eventService.list(user, projectId, env, range, limit);
    }
}
