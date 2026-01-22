package com.telemetryai.backend.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Component
public class OpenAiClient {
    private static final String OPENAI_URL = "https://api.openai.com/v1/responses";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final OpenAiProperties properties;

    public OpenAiClient(ObjectMapper objectMapper, OpenAiProperties properties) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    public String createResponse(String prompt) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("model", properties.getModel());
        payload.put("temperature", properties.getTemperature());
        payload.put("input", prompt);

        ArrayNode modalities = objectMapper.createArrayNode();
        modalities.add("text");
        payload.set("modalities", modalities);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        HttpEntity<String> request = new HttpEntity<>(payload.toString(), headers);
        try {
            String response = restTemplate.postForObject(OPENAI_URL, request, String.class);
            return extractText(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI request failed");
        }
    }

    private String extractText(String responseBody) {
        try {
            var root = objectMapper.readTree(responseBody);
            var output = root.get("output");
            if (output == null || !output.isArray() || output.isEmpty()) {
                throw new IllegalStateException("Empty output");
            }
            var content = output.get(0).get("content");
            if (content == null || !content.isArray() || content.isEmpty()) {
                throw new IllegalStateException("Missing content");
            }
            return content.get(0).get("text").asText();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "OpenAI response parse failed");
        }
    }
}
