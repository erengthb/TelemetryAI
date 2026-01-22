package com.telemetryai.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class AiReportDataLoader {
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AiReportDataLoader(NamedParameterJdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    public Map<String, Object> loadOverview(UUID projectId, String envName, LocalDate start, LocalDate end) {
        String sql = """
            select sum(total_events) as total_events,
                   sum(unique_players) as unique_players,
                   sum(sessions_started) as sessions_started
            from daily_project_metrics
            where project_id = :projectId
              and env_name = :envName
              and metric_date >= :start
              and metric_date <= :end
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("projectId", projectId)
            .addValue("envName", envName)
            .addValue("start", start)
            .addValue("end", end);

        return jdbcTemplate.query(sql, params, rs -> {
            if (!rs.next()) {
                return Map.of();
            }
            Map<String, Object> map = new HashMap<>();
            map.put("total_events", rs.getLong("total_events"));
            map.put("unique_players", rs.getLong("unique_players"));
            map.put("sessions_started", rs.getLong("sessions_started"));
            return map;
        });
    }

    public ArrayNode loadFunnel(UUID projectId, String envName, LocalDate start, LocalDate end) {
        String sql = """
            select level_id,
                   sum(starts) as starts,
                   sum(ends_success) as ends_success,
                   sum(ends_fail) as ends_fail,
                   sum(ends_quit) as ends_quit,
                   avg(avg_duration_sec) as avg_duration_sec
            from daily_level_funnel
            where project_id = :projectId
              and env_name = :envName
              and metric_date >= :start
              and metric_date <= :end
            group by level_id
            order by level_id asc
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("projectId", projectId)
            .addValue("envName", envName)
            .addValue("start", start)
            .addValue("end", end);

        ArrayNode array = objectMapper.createArrayNode();
        jdbcTemplate.query(sql, params, (ResultSet rs) -> {
            while (rs.next()) {
                ObjectNode node = objectMapper.createObjectNode();
                node.put("levelId", rs.getString("level_id"));
                node.put("starts", rs.getLong("starts"));
                node.put("endsSuccess", rs.getLong("ends_success"));
                node.put("endsFail", rs.getLong("ends_fail"));
                node.put("endsQuit", rs.getLong("ends_quit"));
                node.put("avgDurationSec", rs.getDouble("avg_duration_sec"));
                array.add(node);
            }
        });
        return array;
    }

    public ArrayNode loadEventNameSummary(UUID projectId, String envName, LocalDate start, LocalDate end) {
        String sql = """
            select event_name, sum(count) as total
            from daily_event_name_metrics
            where project_id = :projectId
              and env_name = :envName
              and metric_date >= :start
              and metric_date <= :end
            group by event_name
            order by total desc
            limit 20
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("projectId", projectId)
            .addValue("envName", envName)
            .addValue("start", start)
            .addValue("end", end);

        ArrayNode array = objectMapper.createArrayNode();
        jdbcTemplate.query(sql, params, (ResultSet rs) -> {
            while (rs.next()) {
                ObjectNode node = objectMapper.createObjectNode();
                node.put("eventName", rs.getString("event_name"));
                node.put("count", rs.getLong("total"));
                array.add(node);
            }
        });
        return array;
    }

    public ArrayNode loadQuarantineSummary(UUID projectId, String envName, LocalDate start, LocalDate end) {
        String sql = """
            select reason, count(*) as total
            from (
                select jsonb_array_elements_text(reasons::jsonb) as reason
                from quarantine_events
                where project_id = :projectId
                  and env_name = :envName
                  and received_at::date >= :start
                  and received_at::date <= :end
            ) as reason_rows
            group by reason
            order by total desc
            """;
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("projectId", projectId)
            .addValue("envName", envName)
            .addValue("start", start)
            .addValue("end", end);

        ArrayNode array = objectMapper.createArrayNode();
        jdbcTemplate.query(sql, params, (ResultSet rs) -> {
            while (rs.next()) {
                ObjectNode node = objectMapper.createObjectNode();
                node.put("reason", rs.getString("reason"));
                node.put("count", rs.getLong("total"));
                array.add(node);
            }
        });
        return array;
    }

    public JsonNode buildInput(UUID projectId, String envName, LocalDate start, LocalDate end) {
        Map<String, Object> overview = loadOverview(projectId, envName, start, end);
        ArrayNode funnel = loadFunnel(projectId, envName, start, end);
        ArrayNode eventNames = loadEventNameSummary(projectId, envName, start, end);
        ArrayNode quarantine = loadQuarantineSummary(projectId, envName, start, end);

        ObjectNode root = objectMapper.createObjectNode();
        root.put("periodStart", start.toString());
        root.put("periodEnd", end.toString());
        root.set("overview", objectMapper.valueToTree(overview));
        root.set("funnel", funnel);
        root.set("eventNames", eventNames);
        root.set("quarantine", quarantine);
        return root;
    }
}
