package com.example.user_management.configuration;

import com.example.user_management.security.CustomUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty(); // Return an empty Optional if not authenticated
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customuserdetails) {
            return Optional.ofNullable(customuserdetails.getUsername());
        } else {
            // Handle the case where the principal is not a CustomUserDetails
            return Optional.empty();
        }

    }
}
