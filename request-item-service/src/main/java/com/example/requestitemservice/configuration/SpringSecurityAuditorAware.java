package com.example.generalservice.configuration;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@RequiredArgsConstructor
public class SpringSecurityAuditorAware implements AuditorAware<String> {
    private final HttpServletRequest request;

    @Override
    public Optional<String> getCurrentAuditor() {
        String userId = request.getHeader("X-User-Id");
        return Optional.ofNullable(userId);
    }
}
