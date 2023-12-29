package com.example.service_registry.config;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.service_registry.dto.AuthenticationResponse;
import com.example.service_registry.dto.LoginRequest;
import com.example.service_registry.utils.Jwt;
import com.example.service_registry.utils.UserManagementClient;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

	private final UserManagementClient userManagementClient;
	private final Jwt jwt;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		LoginRequest loginRequest = new LoginRequest(username, password);
		AuthenticationResponse jwtResponse;

		try {
			jwtResponse = userManagementClient.login(loginRequest);
		} catch (Exception e) {
			throw new AuthenticationServiceException("Error during authentication: " + e.getMessage(), e);
		}

		if (jwtResponse == null) {
			throw new AuthenticationServiceException("Authentication response is null");
		}

		String accessToken = jwtResponse.getAccessToken();
		log.info("{} [{} jwtToken:---------------------------------]", accessToken);

		if (accessToken != null) {
			Claims claims = jwt.validToken(accessToken);

			// Extract authorities from the JWT claims, assuming they are stored as a list
			// of strings
			List<?> rawAuthoritiesClaim = claims.get("authorities", List.class);

			if (rawAuthoritiesClaim != null) {
				List<String> authoritiesClaim = rawAuthoritiesClaim.stream().map(Object::toString).toList();

				List<GrantedAuthority> authorities = authoritiesClaim.stream().map(SimpleGrantedAuthority::new)
						.map(GrantedAuthority.class::cast) // Explicitly cast to GrantedAuthority
						.toList();

				UserDetails userDetails = new User(username, password, authorities);
				return new UsernamePasswordAuthenticationToken(userDetails, password, authorities);
			} else {
				throw new BadCredentialsException("Authentication failed");
			}
		}

		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
