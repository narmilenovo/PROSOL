package com.example.api_gateway.security;

import java.util.List;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.example.api_gateway.util.Jwt;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final Jwt jwtUtil;

    @Override
    // @SuppressWarnings("unchecked")
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String email = jwtUtil.extractUsername(authToken);
        return Mono.just(jwtUtil.validateToken(authToken))
                .filter(valid -> valid)
                .switchIfEmpty(Mono.empty())
                .map(valid -> {
                    List<String> authorities = jwtUtil.extractAuthorities(authToken);
                    return new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            authorities.stream().map(SimpleGrantedAuthority::new).toList());
                });
    }
}