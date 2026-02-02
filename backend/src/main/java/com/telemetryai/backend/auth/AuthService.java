package com.telemetryai.backend.auth;

import com.telemetryai.backend.security.JwtService;
import com.telemetryai.backend.user.OrgMemberEntity;
import com.telemetryai.backend.user.OrgMemberRepository;
import com.telemetryai.backend.user.UserEntity;
import com.telemetryai.backend.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final OrgMemberRepository orgMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            OrgMemberRepository orgMemberRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.orgMemberRepository = orgMemberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResult authenticate(LoginRequest request) {
        UserEntity user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        List<OrgMemberEntity> memberships = orgMemberRepository.findByUserId(user.getId());
        if (memberships.isEmpty()) {
            throw new IllegalArgumentException("No org membership");
        }

        String role = resolveRole(memberships);
        String token = jwtService.generateToken(user.getId().toString(), user.getEmail(), role);
        return new AuthResult(token, user.getId().toString(), user.getEmail(), role);
    }

    private String resolveRole(List<OrgMemberEntity> memberships) {
        boolean isAdmin = memberships.stream()
            .anyMatch(m -> "admin".equalsIgnoreCase(m.getRole()));
        return isAdmin ? "admin" : "viewer";
    }
}
