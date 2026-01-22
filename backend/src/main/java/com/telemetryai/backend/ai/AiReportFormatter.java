package com.telemetryai.backend.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class AiReportFormatter {
    private final ObjectMapper objectMapper;

    public AiReportFormatter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String buildPrompt(JsonNode input, String explanation) {
        ObjectNode root = objectMapper.createObjectNode();
        root.set("input", input);
        root.put("explanation", explanation);
        root.put("output_format", outputSchema());
        return root.toPrettyString();
    }

    public JsonNode parseResponse(String content) {
        try {
            return objectMapper.readTree(content);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI JSON parse failed");
        }
    }

    public void validateOutput(JsonNode output) {
        if (output == null || !output.isObject()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI output invalid");
        }
        if (!output.has("summary") || !output.get("summary").isTextual()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI output missing summary");
        }
        if (!output.has("findings") || !output.get("findings").isArray()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI output missing findings");
        }
        for (JsonNode finding : output.get("findings")) {
            if (!finding.has("title") || !finding.get("title").isTextual()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI finding missing title");
            }
            if (!finding.has("description") || !finding.get("description").isTextual()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI finding missing description");
            }
            if (!finding.has("evidence") || !finding.get("evidence").isArray()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI finding missing evidence");
            }
            for (JsonNode evidence : finding.get("evidence")) {
                if (!evidence.has("metric") || !evidence.get("metric").isTextual()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI evidence missing metric");
                }
                if (!evidence.has("value")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AI evidence missing value");
                }
            }
        }
    }

    private String outputSchema() {
        List<String> lines = new ArrayList<>();
        lines.add("{" );
        lines.add("  summary: string,");
        lines.add("  findings: [");
        lines.add("    {");
        lines.add("      title: string,");
        lines.add("      description: string,");
        lines.add("      evidence: [");
        lines.add("        { metric: string, value: number|string, period: string? }");
        lines.add("      ]");
        lines.add("    }");
        lines.add("  ]");
        lines.add("}");
        return String.join("\n", lines);
    }
}
