package com.telemetryai.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.telemetryai.backend.project.ProjectEntity;
import com.telemetryai.backend.project.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class AiReportService {
    private static final List<String> ALLOWED_ENVS = List.of("dev", "stage", "prod");

    private final AiReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final AiReportDataLoader dataLoader;
    private final AiReportFormatter formatter;
    private final OpenAiClient openAiClient;

    public AiReportService(
            AiReportRepository reportRepository,
            ProjectRepository projectRepository,
            AiReportDataLoader dataLoader,
            AiReportFormatter formatter,
            OpenAiClient openAiClient
    ) {
        this.reportRepository = reportRepository;
        this.projectRepository = projectRepository;
        this.dataLoader = dataLoader;
        this.formatter = formatter;
        this.openAiClient = openAiClient;
    }

    @Transactional
    public void generateDaily(LocalDate targetDate) {
        generate(targetDate, targetDate, "daily", "prod");
    }

    @Transactional
    public void generateWeekly(LocalDate weekEnd) {
        generate(weekEnd.minusDays(7), weekEnd.minusDays(1), "weekly", "prod");
    }

    @Transactional
    public void generate(LocalDate start, LocalDate end, String reportType, String envName) {
        if (!ALLOWED_ENVS.contains(envName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid env");
        }

        List<ProjectEntity> projects = projectRepository.findAll();
        for (ProjectEntity project : projects) {
            JsonNode input = dataLoader.buildInput(project.getId(), envName, start, end);
            String explanation = project.getAiExplanation();
            if (explanation == null || explanation.isBlank()) {
                explanation = "Verileri yorumla, sadece metriklerle kanitla. Tavsiye verme.";
            }

            String prompt = formatter.buildPrompt(input, explanation);
            try {
                String responseText = openAiClient.createResponse(prompt);
                JsonNode output = formatter.parseResponse(responseText);
                formatter.validateOutput(output);
                saveReport(project.getId(), envName, reportType, start, end, output.toString(), "completed");
            } catch (Exception e) {
                saveReport(project.getId(), envName, reportType, start, end, "{}", "failed");
            }
        }
    }

    private void saveReport(
            java.util.UUID projectId,
            String envName,
            String reportType,
            LocalDate start,
            LocalDate end,
            String reportJson,
            String status
    ) {
        AiReportEntity entity = new AiReportEntity();
        entity.setProjectId(projectId);
        entity.setEnvName(envName);
        entity.setReportType(reportType);
        entity.setPeriodStart(start);
        entity.setPeriodEnd(end);
        entity.setStatus(status);
        entity.setReportJson(reportJson);
        entity.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        reportRepository.save(entity);
    }
}
