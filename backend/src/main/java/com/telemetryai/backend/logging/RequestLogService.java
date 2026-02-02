package com.telemetryai.backend.logging;

import com.telemetryai.backend.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.security.MessageDigest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
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
        entity.setIp(safe(maskIp(resolveIp(request))));
        entity.setUserAgent(safe(hashUserAgent(request.getHeader("User-Agent"))));
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

    private String maskIp(String ip) {
        if (ip == null || ip.isBlank()) {
            return null;
        }
        try {
            InetAddress address = InetAddress.getByName(ip);
            byte[] bytes = address.getAddress();
            if (bytes.length == 4) {
                bytes[3] = 0;
                return InetAddress.getByAddress(bytes).getHostAddress();
            }
            if (bytes.length == 16) {
                for (int i = 6; i < 16; i++) {
                    bytes[i] = 0;
                }
                return InetAddress.getByAddress(bytes).getHostAddress();
            }
        } catch (Exception ignored) {
        }
        return ip;
    }

    private String hashUserAgent(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(userAgent.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return "sha256:" + HexFormat.of().formatHex(hashed);
        } catch (Exception ignored) {
            return "sha256:unknown";
        }
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
