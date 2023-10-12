package com.example.api_gateway.filter;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
    protected static final List<String> swagger = List.of(
            "/v3/api-docs");
    protected static final List<String> user = List.of(
            "/auth/login",
            "/auth/logout",
            "/auth/validateToken/**",
            "/auth/refresh-token/**",
            "/saveUser", "/saveRole", "/savePrivilege",
            "/forgotPassword**", "/resetPassword**"

    );
    protected static final List<String> general = List.of(
            "/getAllUomTrue"
    );
    protected static final List<String> openApiEndpoints = new ArrayList<>();

    static {
        openApiEndpoints.addAll(swagger);
        openApiEndpoints.addAll(user);
        openApiEndpoints.addAll(general);
    }

    protected Predicate<ServerHttpRequest> isSecured =
            serverHttpRequest -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> serverHttpRequest.getURI().getPath().contains(uri));
}
