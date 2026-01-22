package com.telemetryai.backend.dashboard;

import java.time.LocalDate;

public class DailyMetricPoint {
    private final LocalDate date;
    private final long totalEvents;
    private final long uniquePlayers;
    private final long sessionsStarted;

    public DailyMetricPoint(LocalDate date, long totalEvents, long uniquePlayers, long sessionsStarted) {
        this.date = date;
        this.totalEvents = totalEvents;
        this.uniquePlayers = uniquePlayers;
        this.sessionsStarted = sessionsStarted;
    }

    public LocalDate getDate() {
        return date;
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
}
