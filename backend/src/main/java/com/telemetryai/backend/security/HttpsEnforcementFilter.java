package com.telemetryai.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
public class HttpsEnforcementFilter extends OncePerRequestFilter {
    private static final Set<String> LOCAL_HOSTS = Set.of("localhost", "127.0.0.1", "::1");

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (isSecure(request) || isLocalRequest(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "HTTPS required");
    }

    private boolean isSecure(HttpServletRequest request) {
        if (request.isSecure()) {
            return true;
        }
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return "https".equalsIgnoreCase(forwardedProto);
    }

    private boolean isLocalRequest(HttpServletRequest request) {
        String host = request.getServerName();
        if (host != null && LOCAL_HOSTS.contains(host)) {
            return true;
        }
        String remoteAddr = request.getRemoteAddr();
        return remoteAddr != null && LOCAL_HOSTS.contains(remoteAddr);
    }
}
