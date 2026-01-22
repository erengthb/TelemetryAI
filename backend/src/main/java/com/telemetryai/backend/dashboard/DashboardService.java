package com.telemetryai.backend.dashboard;

import com.telemetryai.backend.project.ProjectEntity;
import com.telemetryai.backend.project.ProjectRepository;
import com.telemetryai.backend.user.OrgMemberRepository;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class DashboardService {
    private static final List<String> ALLOWED_ENVS = List.of("dev", "stage", "prod");
    private static final int DEFAULT_RANGE_DAYS = 7;

    private final ProjectRepository projectRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public DashboardService(
            ProjectRepository projectRepository,
            OrgMemberRepository orgMemberRepository,
            NamedParameterJdbcTemplate jdbcTemplate
    ) {
        this.projectRepository = projectRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public DashboardOverviewResponse overview(UserEntity user, UUID projectId, String envName, String range) {
        requireMember(user, projectId);
        validateEnv(envName);

        DashboardRangeParser.Range window = DashboardRangeParser.parse(range, DEFAULT_RANGE_DAYS);
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("projectId", projectId)
            .addValue("envName", envName)
            .addValue("from", window.from().toLocalDateTime())
            .addValue("to", window.to().toLocalDateTime());

        List<DailyMetricPoint> series = jdbcTemplate.query(
            """
            select metric_date, total_events, unique_players, sessions_started
            from daily_project_metrics
            where project_id = :projectId
              and env_name = :envName
              and metric_date >= :from::date
              and metric_date <= :to::date
            order by metric_date asc
            """,
            params,
            new DailyMetricRowMapper()
        );

        long totalEvents = series.stream().mapToLong(DailyMetricPoint::getTotalEvents).sum();
        long uniquePlayers = series.stream().mapToLong(DailyMetricPoint::getUniquePlayers).sum();
        long sessionsStarted = series.stream().mapToLong(DailyMetricPoint::getSessionsStarted).sum();

        return new DashboardOverviewResponse(totalEvents, uniquePlayers, sessionsStarted, series);
    }

    public DashboardFunnelResponse funnel(UserEntity user, UUID projectId, String envName, String range) {
        requireMember(user, projectId);
        validateEnv(envName);

        DashboardRangeParser.Range window = DashboardRangeParser.parse(range, DEFAULT_RANGE_DAYS);
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("projectId", projectId)
            .addValue("envName", envName)
            .addValue("from", window.from().toLocalDateTime())
            .addValue("to", window.to().toLocalDateTime());

        List<FunnelRowResponse> rows = jdbcTemplate.query(
            """
            select level_id,
                   sum(starts) as starts,
                   sum(ends_success) as ends_success,
                   sum(ends_fail) as ends_fail,
                   sum(ends_quit) as ends_quit,
                   avg(avg_duration_sec) as avg_duration_sec
            from daily_level_funnel
            where project_id = :projectId
              and env_name = :envName
              and metric_date >= :from::date
              and metric_date <= :to::date
            group by level_id
            order by level_id asc
            """,
            params,
            new FunnelRowMapper()
        );

        return new DashboardFunnelResponse(rows);
    }

    private ProjectEntity requireMember(UserEntity user, UUID projectId) {
        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        orgMemberRepository.findByUserIdAndOrgId(user.getId(), project.getOrgId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));

        return project;
    }

    private void validateEnv(String envName) {
        if (envName == null || !ALLOWED_ENVS.contains(envName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid env");
        }
    }
}
