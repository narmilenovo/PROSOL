package com.example.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    protected static final List<String> swagger = List.of(
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/configuration/ui/**",
            "/configuration/security/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    );
    protected static final List<String> eurekaServer = List.of(
            "/eureka/web/**"
    );

    protected static final List<String> openApiEndpoints = new ArrayList<>();

    static {
        openApiEndpoints.addAll(swagger);
        openApiEndpoints.addAll(eurekaServer);
    }

    protected Predicate<ServerHttpRequest> isSecured =
            serverHttpRequest -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}
