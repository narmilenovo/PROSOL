package com.example.api_gateway.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.example.api_gateway.util.Jwt;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@Component
@RequiredArgsConstructor
public class SecurityContextRepository implements ServerSecurityContextRepository {

	private final AuthenticationManager authenticationManager;
	private final Jwt jwtUtil;

	@Override
	public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Mono<SecurityContext> load(ServerWebExchange exchange) {
		return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
				.filter(authHeader -> authHeader.startsWith("Bearer ")).flatMap(authHeader -> {
					String authToken = authHeader.substring(7);

					String email = jwtUtil.extractUsername(authToken);
					List<String> authorities = jwtUtil.extractAuthorities(authToken);

					// Convert authorities to GrantedAuthority objects
					List<GrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new)
							.collect(Collectors.toList());

					Authentication auth = new UsernamePasswordAuthenticationToken(email, authToken, grantedAuthorities);

					// Create a new ServerWebExchange with modified headers
					ServerWebExchange exchangeWithHeaders = exchange.mutate()
							.request(exchange.getRequest().mutate().header("X-User-Id", email)
									.header("X-User-Authorities", grantedAuthorities.toString()).build())
							.build();

					return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new)
							.contextWrite(Context.of(ServerWebExchange.class, exchangeWithHeaders));
				});

	}

}