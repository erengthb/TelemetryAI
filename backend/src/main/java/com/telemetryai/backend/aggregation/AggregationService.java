package com.telemetryai.backend.aggregation;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
public class AggregationService {
    private static final String PROJECT_METRICS_SQL = """
        insert into daily_project_metrics (metric_date, project_id, env_name, total_events, unique_players, sessions_started)
        select :metricDate, project_id, env_name,
               count(*) as total_events,
               count(distinct player_id) as unique_players,
               count(*) filter (where event_name = 'session_start') as sessions_started
        from events
        where ts_server >= :start and ts_server < :end
        group by project_id, env_name
        on conflict (metric_date, project_id, env_name)
        do update set total_events = excluded.total_events,
                      unique_players = excluded.unique_players,
                      sessions_started = excluded.sessions_started
        """;

    private static final String EVENT_NAME_METRICS_SQL = """
        insert into daily_event_name_metrics (metric_date, project_id, env_name, event_name, count)
        select :metricDate, project_id, env_name, event_name, count(*)
        from events
        where ts_server >= :start and ts_server < :end
        group by project_id, env_name, event_name
        on conflict (metric_date, project_id, env_name, event_name)
        do update set count = excluded.count
        """;

    private static final String LEVEL_FUNNEL_SQL = """
        insert into daily_level_funnel (
            metric_date, project_id, env_name, level_id,
            starts, ends_success, ends_fail, ends_quit, avg_duration_sec
        )
        select :metricDate, project_id, env_name,
               properties->>'levelId' as level_id,
               count(*) filter (where event_name = 'level_start') as starts,
               count(*) filter (where event_name = 'level_end' and properties->>'result' = 'success') as ends_success,
               count(*) filter (where event_name = 'level_end' and properties->>'result' = 'fail') as ends_fail,
               count(*) filter (where event_name = 'level_end' and properties->>'result' = 'quit') as ends_quit,
               avg((nullif(properties->>'durationSec',''))::double precision)
                   filter (where event_name = 'level_end') as avg_duration_sec
        from events
        where ts_server >= :start and ts_server < :end
          and event_name in ('level_start', 'level_end')
          and properties ? 'levelId'
        group by project_id, env_name, level_id
        on conflict (metric_date, project_id, env_name, level_id)
        do update set starts = excluded.starts,
                      ends_success = excluded.ends_success,
                      ends_fail = excluded.ends_fail,
                      ends_quit = excluded.ends_quit,
                      avg_duration_sec = excluded.avg_duration_sec
        """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public AggregationService(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public void aggregateDaily(LocalDate date) {
        OffsetDateTime start = date.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = start.plusDays(1);

        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("metricDate", date)
            .addValue("start", start)
            .addValue("end", end);

        jdbcTemplate.update(PROJECT_METRICS_SQL, params);
        jdbcTemplate.update(EVENT_NAME_METRICS_SQL, params);
        jdbcTemplate.update(LEVEL_FUNNEL_SQL, params);
    }
}
