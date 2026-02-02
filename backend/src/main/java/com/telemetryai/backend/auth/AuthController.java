package com.telemetryai.backend.auth;

import com.telemetryai.backend.common.RateLimiterService;
import com.telemetryai.backend.config.JwtProperties;
import com.telemetryai.backend.user.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {
    private static final int LOGIN_RPS = 1;
    private static final int LOGIN_BURST = 5;

    private final AuthService authService;
    private final JwtProperties jwtProperties;
    private final RateLimiterService rateLimiter = new RateLimiterService(LOGIN_RPS, LOGIN_BURST);

    public AuthController(AuthService authService, JwtProperties jwtProperties) {
        this.authService = authService;
        this.jwtProperties = jwtProperties;
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        if (!rateLimiter.tryConsume(resolveRateKey(httpRequest, request.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded");
        }

        AuthResult result = authService.authenticate(request);
        ResponseCookie cookie = ResponseCookie.from("ta_token", result.getToken())
            .httpOnly(true)
            .secure(isSecure(httpRequest))
            .sameSite("Lax")
            .path("/")
            .maxAge(Duration.ofMinutes(jwtProperties.getTtlMinutes()))
            .build();
        httpResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return new LoginResponse(result.getUserId(), result.getEmail(), result.getRole());
    }

    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {
        UserEntity user = (UserEntity) authentication.getPrincipal();
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", "").toLowerCase())
            .orElse("viewer");

        return new MeResponse(user.getId().toString(), user.getEmail(), role);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public void handleAuthError() {
        // Intentionally empty: return 401 without body.
    }

    private boolean isSecure(HttpServletRequest request) {
        if (request.isSecure()) {
            return true;
        }
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        return "https".equalsIgnoreCase(forwardedProto);
    }

    private String resolveRateKey(HttpServletRequest request, String email) {
        String forwarded = request.getHeader("X-Forwarded-For");
        String ip = forwarded != null && !forwarded.isBlank()
            ? forwarded.split(",")[0].trim()
            : request.getRemoteAddr();
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase();
        return ip + ":" + normalizedEmail;
    }
}
