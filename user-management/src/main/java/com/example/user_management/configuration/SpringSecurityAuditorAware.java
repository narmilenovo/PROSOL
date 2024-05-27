package com.example.user_management.configuration;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.user_management.security.MyUserPrincipal;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

	public Optional<String> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			return Optional.empty(); // Return an empty Optional if not authenticated
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof MyUserPrincipal myUserPrincipal) {
			return Optional.ofNullable(myUserPrincipal.getUsername());
		} else {
			// Handle the case where the principal is not a CustomUserDetails
			return Optional.empty();
		}

	}
}
