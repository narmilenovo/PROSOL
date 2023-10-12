package com.example.api_gateway.filter;

import com.example.api_gateway.util.Jwt;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {
    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private Jwt jwt;
    @Autowired
    private AuthorizationService authorizationService;


    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            if (routeValidator.isSecured.test(exchange.getRequest())) {
                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new RuntimeException("Missing Authorization header Bearer Token");
                }
                String authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (authHeaders != null && authHeaders.startsWith("Bearer ")) {
                    authHeaders = authHeaders.substring(7);
                }
                try {
                    Claims claims = jwt.validToken(authHeaders);
                    String email = claims.getSubject();
                    exchange.getRequest().mutate().header("X-User-Id", email);
                    System.out.println("UserName: " + email);

                    List<String> authorities = jwt.extractAuthorities(authHeaders);
                    List<String> roles = new ArrayList<>();
                    List<String> privileges = new ArrayList<>();

                    for (String authority : authorities) {
                        if (authority.startsWith("ROLE_")) {
                            roles.add(authority);
                        } else {
                            privileges.add(authority);
                        }
                    }
                    // You can now use 'roles' and 'privileges' as needed.
                    exchange.getRequest().mutate().header("X-User-Roles", String.join(",", roles));
                    exchange.getRequest().mutate().header("X-User-Authorities", String.join(",", privileges));
                    // You can now use 'roles' and 'privileges' as needed.
                    System.out.println("Roles: " + roles);
                    System.out.println("Privileges: " + privileges);

                    String requestPath = exchange.getRequest().getURI().getPath();
                    if (authorizationService.hasRequiredRole(requestPath, roles)) {
                        // Authorized: Allow the request to proceed
                        return chain.filter(exchange);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Unauthorized access to an application");
                }
            }
            return chain.filter(exchange);
        });
    }

    public static class Config {
        // empty class as I don't need any particular configuration
    }
}
