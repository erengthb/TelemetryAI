package com.telemetryai.backend.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {
    private final RequestLogService requestLogService;

    public RequestLoggingFilter(RequestLogService requestLogService) {
        this.requestLogService = requestLogService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (!"/v1/events/batch".equals(path) || !"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        long start = System.currentTimeMillis();
        Exception error = null;
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            error = ex;
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - start;
            try {
                requestLogService.logRequest(request, response, duration, error);
            } catch (Exception ignored) {
            }
        }
    }
}
