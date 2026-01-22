package com.telemetryai.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telemetryai.backend.project.ProjectEntity;
import com.telemetryai.backend.project.ProjectRepository;
import com.telemetryai.backend.user.OrgMemberRepository;
import com.telemetryai.backend.user.UserEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class AiReportQueryService {
    private final AiReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final ObjectMapper objectMapper;

    public AiReportQueryService(
            AiReportRepository reportRepository,
            ProjectRepository projectRepository,
            OrgMemberRepository orgMemberRepository,
            ObjectMapper objectMapper
    ) {
        this.reportRepository = reportRepository;
        this.projectRepository = projectRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.objectMapper = objectMapper;
    }

    public List<JsonNode> list(UserEntity user, UUID projectId, String envName, String reportType) {
        ProjectEntity project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        orgMemberRepository.findByUserIdAndOrgId(user.getId(), project.getOrgId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a member"));

        return reportRepository.findByProjectIdAndEnvNameAndReportTypeOrderByPeriodEndDesc(
            projectId,
            envName,
            reportType
        ).stream().map(report -> parseJson(report.getReportJson())).toList();
    }

    private JsonNode parseJson(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            return objectMapper.createObjectNode();
        }
    }
}
