package com.telemetryai.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrgMemberRepository extends JpaRepository<OrgMemberEntity, OrgMemberId> {
    List<OrgMemberEntity> findByUserId(UUID userId);
}
