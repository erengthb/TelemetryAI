package com.telemetryai.backend.apikey;

import com.telemetryai.backend.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/projects/{projectId}/keys")
public class ApiKeyController {
    private final ApiKeyService apiKeyService;

    public ApiKeyController(ApiKeyService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    @PostMapping("/rotate")
    public ApiKeyCreateResponse rotateKey(
            @PathVariable UUID projectId,
            @RequestParam("env") String envName,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return apiKeyService.rotateKey(user, projectId, envName);
    }

    @PostMapping("/revoke")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeKey(
            @PathVariable UUID projectId,
            @RequestParam("env") String envName,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        apiKeyService.revokeKey(user, projectId, envName);
    }

    @GetMapping
    public List<ApiKeyResponse> listKeys(
            @PathVariable UUID projectId,
            @RequestParam(value = "env", required = false) String envName,
            Authentication authentication
    ) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return apiKeyService.listKeys(user, projectId, envName);
    }
}
