package com.telemetryai.backend.dashboard;

import com.telemetryai.backend.user.UserEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/projects/{projectId}/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/overview")
    public DashboardOverviewResponse overview(
            @PathVariable UUID projectId,
            @RequestParam("env") String envName,
            @RequestParam(value = "range", required = false) String range,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return dashboardService.overview(user, projectId, envName, range);
    }

    @GetMapping("/funnel")
    public DashboardFunnelResponse funnel(
            @PathVariable UUID projectId,
            @RequestParam("env") String envName,
            @RequestParam(value = "range", required = false) String range,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return dashboardService.funnel(user, projectId, envName, range);
    }
}
