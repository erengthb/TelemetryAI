package com.telemetryai.backend.org;

import com.telemetryai.backend.user.OrgMemberEntity;
import com.telemetryai.backend.user.OrgMemberRepository;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class OrgService {
    private final OrgRepository orgRepository;
    private final OrgMemberRepository orgMemberRepository;

    public OrgService(OrgRepository orgRepository, OrgMemberRepository orgMemberRepository) {
        this.orgRepository = orgRepository;
        this.orgMemberRepository = orgMemberRepository;
    }

    public OrgResponse createOrg(UserEntity user, CreateOrgRequest request) {
        OrgEntity org = new OrgEntity();
        org.setId(UUID.randomUUID());
        org.setName(request.getName().trim());
        org.setStatus("active");
        org.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        orgRepository.save(org);

        OrgMemberEntity membership = new OrgMemberEntity();
        membership.setOrgId(org.getId());
        membership.setUserId(user.getId());
        membership.setRole("admin");
        orgMemberRepository.save(membership);

        return OrgResponse.from(org);
    }

    public List<OrgResponse> listOrgs(UserEntity user) {
        List<UUID> orgIds = orgMemberRepository.findByUserId(user.getId()).stream()
            .map(OrgMemberEntity::getOrgId)
            .distinct()
            .toList();

        if (orgIds.isEmpty()) {
            return List.of();
        }

        return orgRepository.findAllById(orgIds).stream()
            .map(OrgResponse::from)
            .toList();
    }
}
