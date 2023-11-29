package com.example.api_gateway.config;

import com.example.api_gateway.security.AuthenticationManager;
import com.example.api_gateway.security.SecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    protected static final List<String> SWAGGER = List.of(
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/configuration/ui/**",
            "/configuration/security/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    );
    protected static final List<String> USER = List.of(
            "/user/auth/**",
            "/user/v3/api-docs",
            "/actuator/**"

    );
    protected static final List<String> PERMIT_ALL_URLS = new ArrayList<>();
    private static final List<String> GENERAL = List.of(
            "/general/v3/api-docs"
    );
    private static final List<String> SALES = List.of(
            "/sales/v3/api-docs"
    );
    private static final List<String> PLANT = List.of(
            "/plant/v3/api-docs"
    );
    private static final List<String> MRP = List.of(
            "/mrp/v3/api-docs"
    );
    private static final List<String> VENDOR = List.of(
            "/vendor/v3/api-docs"
    );
    private static final List<String> SETTINGS = List.of(
            "/setting/v3/api-docs"
    );
    private static final List<String> VALUE = List.of(
            "/value/v3/api-docs"
    );

    static {
        PERMIT_ALL_URLS.addAll(SWAGGER);
        PERMIT_ALL_URLS.addAll(USER);
        PERMIT_ALL_URLS.addAll(GENERAL);
        PERMIT_ALL_URLS.addAll(PLANT);
        PERMIT_ALL_URLS.addAll(SALES);
        PERMIT_ALL_URLS.addAll(VENDOR);
        PERMIT_ALL_URLS.addAll(MRP);
        PERMIT_ALL_URLS.addAll(SETTINGS);
        PERMIT_ALL_URLS.addAll(VALUE);
    }

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint((swe, e) ->
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                        .accessDeniedHandler((swe, e) ->
                                Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers(PERMIT_ALL_URLS.toArray(new String[0])).permitAll()
                        .pathMatchers("/user/getAllUsers").hasRole("Admin")
                        .anyExchange().authenticated()
                );
        return http.build();
    }
}