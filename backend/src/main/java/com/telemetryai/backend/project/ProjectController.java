package com.telemetryai.backend.project;

import com.telemetryai.backend.user.UserEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/projects")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return projectService.createProject(user, request);
    }

    @GetMapping
    public List<ProjectResponse> listProjects(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return projectService.listProjects(user);
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getProject(@PathVariable UUID projectId, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return projectService.getProject(user, projectId);
    }

    @GetMapping("/{projectId}/environments")
    public List<EnvironmentResponse> listEnvironments(
            @PathVariable UUID projectId,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return projectService.listEnvironments(user, projectId);
    }
}
