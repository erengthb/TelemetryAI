package com.telemetryai.backend.apikey;

import com.telemetryai.backend.project.ProjectEntity;
import com.telemetryai.backend.project.ProjectRepository;
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
public class ApiKeyService {
    private static final List<String> ALLOWED_ENVS = List.of("dev", "stage", "prod");

    private final ApiKeyRepository apiKeyRepository;
    private final ProjectRepository projectRepository;
    private final OrgMemberRepository orgMemberRepository;

    public ApiKeyService(
            ApiKeyRepository apiKeyRepository,
            ProjectRepository projectRepository,
            OrgMemberRepository orgMemberRepository
    ) {
        this.apiKeyRepository = apiKeyRepository;
        this.projectRepository = projectRepository;
        this.orgMemberRepository = orgMemberRepository;
    }

    @Transactional
    public ApiKeyCreateResponse rotateKey(UserEntity user, UUID projectId, String envName) {
        validateEnvName(envName);
        requireAdmin(user, projectId);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<ApiKeyEntity> existing = apiKeyRepository.findByProjectIdAndEnvName(projectId, envName);
        existing.stream()
            .filter(key -> key.getRevokedAt() == null)
            .forEach(key -> key.setRevokedAt(now));
        apiKeyRepository.saveAll(existing);

        String apiKey = ApiKeyUtil.generateKey();
        String hash = ApiKeyUtil.hashKey(apiKey, ApiKeyUtil.newSalt());
        String last4 = ApiKeyUtil.last4(apiKey);

        ApiKeyEntity entity = new ApiKeyEntity();
        entity.setId(UUID.randomUUID());
        entity.setProjectId(projectId);
        entity.setEnvName(envName);
        entity.setKeyHash(hash);
        entity.setKeyLast4(last4);
        entity.setCreatedAt(now);
        apiKeyRepository.save(entity);

        return new ApiKeyCreateResponse(
            entity.getId(),
            envName,
            apiKey,
            ApiKeyUtil.maskedKey(last4),
            entity.getCreatedAt()
        );
    }

    @Transactional
    public void revokeKey(UserEntity user, UUID projectId, String envName) {
        validateEnvName(envName);
        requireAdmin(user, projectId);

        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<ApiKeyEntity> existing = apiKeyRepository.findByProjectIdAndEnvName(projectId, envName);
        boolean revokedAny = false;
        for (ApiKeyEntity key : existing) {
            if (key.getRevokedAt() == null) {
                key.setRevokedAt(now);
                revokedAny = true;
            }
        }
        if (!revokedAny) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Active key not found");
        }
        apiKeyRepository.saveAll(existing);
    }

    public List<ApiKeyResponse> listKeys(UserEntity user, UUID projectId, String envName) {
        validateEnvNameOptional(envName);
        requireAdmin(user, projectId);

        List<ApiKeyEntity> keys = envName == null
            ? apiKeyRepository.findByProjectId(projectId)
            : apiKeyRepository.findByProjectIdAndEnvName(projectId, envName);

        return keys.stream()
            .map(ApiKeyResponse::from)
            .toList();
    }

    private void validateEnvName(String envName) {
        if (envName == null || !ALLOWED_ENVS.contains(envName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid env");
        }
    }

    private void validateEnvNameOptional(String envName) {
        if (envName == null) {
            return;
        }
        validateEnvName(envName);
    }

    private ProjectEntity requireAdmin(UserEntity user, UUID projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        OrgMemberEntity membership = orgMemberRepository.findByUserIdAndOrgId(user.getId(), project.getOrgId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));

        if (!"admin".equalsIgnoreCase(membership.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin role required");
        }

        return project;
    }
}
