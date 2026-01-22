package com.telemetryai.backend.dashboard;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class DailyMetricRowMapper implements RowMapper<DailyMetricPoint> {
    @Override
    public DailyMetricPoint mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocalDate date = rs.getDate("metric_date").toLocalDate();
        long totalEvents = rs.getLong("total_events");
        long uniquePlayers = rs.getLong("unique_players");
        long sessionsStarted = rs.getLong("sessions_started");
        return new DailyMetricPoint(date, totalEvents, uniquePlayers, sessionsStarted);
    }
}
