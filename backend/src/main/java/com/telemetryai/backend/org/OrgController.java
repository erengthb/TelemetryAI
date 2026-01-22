package com.telemetryai.backend.org;

import com.telemetryai.backend.user.UserEntity;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/orgs")
public class OrgController {
    private final OrgService orgService;

    public OrgController(OrgService orgService) {
        this.orgService = orgService;
    }

    @PostMapping
    public OrgResponse createOrg(@Valid @RequestBody CreateOrgRequest request, Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return orgService.createOrg(user, request);
    }

    @GetMapping
    public List<OrgResponse> listOrgs(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        return orgService.listOrgs(user);
    }
}
