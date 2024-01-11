package com.example.dynamic.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    private final HttpServletRequest request;

    @Override
    public Optional<String> getCurrentAuditor() {
        String userId = request.getHeader("X-User-Id");
        return Optional.ofNullable(userId);
    }
}
