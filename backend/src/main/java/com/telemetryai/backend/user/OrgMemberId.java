package com.telemetryai.backend.user;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class OrgMemberId implements Serializable {
    private UUID orgId;
    private UUID userId;

    public OrgMemberId() {
    }

    public OrgMemberId(UUID orgId, UUID userId) {
        this.orgId = orgId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrgMemberId)) {
            return false;
        }
        OrgMemberId that = (OrgMemberId) o;
        return Objects.equals(orgId, that.orgId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orgId, userId);
    }
}
