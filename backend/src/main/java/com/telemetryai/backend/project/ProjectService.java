package com.telemetryai.backend.project;

import com.telemetryai.backend.org.OrgRepository;
import com.telemetryai.backend.user.OrgMemberEntity;
import com.telemetryai.backend.user.OrgMemberRepository;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {
    private static final List<String> DEFAULT_ENVIRONMENTS = List.of("dev", "stage", "prod");

    private final ProjectRepository projectRepository;
    private final EnvironmentRepository environmentRepository;
    private final OrgRepository orgRepository;
    private final OrgMemberRepository orgMemberRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            EnvironmentRepository environmentRepository,
            OrgRepository orgRepository,
            OrgMemberRepository orgMemberRepository
    ) {
        this.projectRepository = projectRepository;
        this.environmentRepository = environmentRepository;
        this.orgRepository = orgRepository;
        this.orgMemberRepository = orgMemberRepository;
    }

    @Transactional
    public ProjectResponse createProject(UserEntity user, CreateProjectRequest request) {
        UUID orgId = request.getOrgId();
        if (!orgRepository.existsById(orgId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Org not found");
        }

        OrgMemberEntity membership = orgMemberRepository.findByUserIdAndOrgId(user.getId(), orgId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));
        if (!"admin".equalsIgnoreCase(membership.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }

        ProjectEntity project = new ProjectEntity();
        project.setId(UUID.randomUUID());
        project.setOrgId(orgId);
        project.setName(request.getName().trim());
        project.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        projectRepository.save(project);

        DEFAULT_ENVIRONMENTS.forEach(envName -> {
            EnvironmentEntity environment = new EnvironmentEntity();
            environment.setId(UUID.randomUUID());
            environment.setProjectId(project.getId());
            environment.setEnvName(envName);
            environmentRepository.save(environment);
        });

        return ProjectResponse.from(project);
    }

    public List<ProjectResponse> listProjects(UserEntity user) {
        List<UUID> orgIds = orgMemberRepository.findByUserId(user.getId()).stream()
            .map(OrgMemberEntity::getOrgId)
            .distinct()
            .toList();

        if (orgIds.isEmpty()) {
            return List.of();
        }

        return projectRepository.findByOrgIdIn(orgIds).stream()
            .map(ProjectResponse::from)
            .toList();
    }

    public ProjectResponse getProject(UserEntity user, UUID projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        orgMemberRepository.findByUserIdAndOrgId(user.getId(), project.getOrgId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));

        return ProjectResponse.from(project);
    }

    public List<EnvironmentResponse> listEnvironments(UserEntity user, UUID projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        orgMemberRepository.findByUserIdAndOrgId(user.getId(), project.getOrgId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));

        return environmentRepository.findByProjectId(projectId).stream()
            .map(EnvironmentResponse::from)
            .toList();
    }
}
