package com.telemetryai.backend.logging;

import com.telemetryai.backend.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class RequestLogService {
    private static final int MAX_FIELD_LENGTH = 2000;

    private final RequestLogRepository repository;

    public RequestLogService(RequestLogRepository repository) {
        this.repository = repository;
    }

    public void logRequest(HttpServletRequest request, HttpServletResponse response, long durationMs, Exception error) {
        RequestLogEntity entity = new RequestLogEntity();
        entity.setTs(OffsetDateTime.now(ZoneOffset.UTC));
        entity.setMethod(safe(request.getMethod()));
        entity.setPath(safe(request.getRequestURI()));
        entity.setQuery(safe(request.getQueryString()));
        entity.setStatus(response.getStatus());
        entity.setDurationMs((int) Math.min(Integer.MAX_VALUE, durationMs));
        entity.setUserId(resolveUserId());
        entity.setIp(safe(resolveIp(request)));
        entity.setUserAgent(safe(request.getHeader("User-Agent")));
        if (error != null) {
            String message = error.getMessage();
            String errorText = error.getClass().getSimpleName() + (message == null ? "" : ": " + message);
            entity.setError(safe(errorText));
        }
        repository.save(entity);
    }

    private UUID resolveUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserEntity user) {
            return user.getId();
        }
        return null;
    }

    private String resolveIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String safe(String value) {
        if (value == null) {
            return null;
        }
        if (value.length() <= MAX_FIELD_LENGTH) {
            return value;
        }
        return value.substring(0, MAX_FIELD_LENGTH);
    }
}
