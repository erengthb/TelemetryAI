package com.telemetryai.backend.dashboard;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FunnelRowMapper implements RowMapper<FunnelRowResponse> {
    @Override
    public FunnelRowResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        String levelId = rs.getString("level_id");
        long starts = rs.getLong("starts");
        long endsSuccess = rs.getLong("ends_success");
        long endsFail = rs.getLong("ends_fail");
        long endsQuit = rs.getLong("ends_quit");
        Double avgDuration = rs.getObject("avg_duration_sec", Double.class);
        return new FunnelRowResponse(levelId, starts, endsSuccess, endsFail, endsQuit, avgDuration);
    }
}
