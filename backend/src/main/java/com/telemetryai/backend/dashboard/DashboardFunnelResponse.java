package com.telemetryai.backend.dashboard;

import java.util.List;

public class DashboardFunnelResponse {
    private final List<FunnelRowResponse> rows;

    public DashboardFunnelResponse(List<FunnelRowResponse> rows) {
        this.rows = rows;
    }

    public List<FunnelRowResponse> getRows() {
        return rows;
    }
}
