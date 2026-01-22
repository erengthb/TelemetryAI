package com.telemetryai.backend.dashboard;

import java.util.List;

public class DashboardOverviewResponse {
    private final long totalEvents;
    private final long uniquePlayers;
    private final long sessionsStarted;
    private final List<DailyMetricPoint> series;

    public DashboardOverviewResponse(
            long totalEvents,
            long uniquePlayers,
            long sessionsStarted,
            List<DailyMetricPoint> series
    ) {
        this.totalEvents = totalEvents;
        this.uniquePlayers = uniquePlayers;
        this.sessionsStarted = sessionsStarted;
        this.series = series;
    }

    public long getTotalEvents() {
        return totalEvents;
    }

    public long getUniquePlayers() {
        return uniquePlayers;
    }

    public long getSessionsStarted() {
        return sessionsStarted;
    }

    public List<DailyMetricPoint> getSeries() {
        return series;
    }
}
