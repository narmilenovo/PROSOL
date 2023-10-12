package com.example.api_gateway.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorizationService {

    private final AuthorizationConfig authorizationConfig;

    @Autowired
    public AuthorizationService(AuthorizationConfig authorizationConfig) {
        this.authorizationConfig = authorizationConfig;
    }

    public boolean hasRequiredRole(String path, List<String> userRoles) {
        for (PathRoleConfig pathRoleConfig : authorizationConfig.getPathRoleConfigList()) {
            if (path.equals(pathRoleConfig.getPath())) {
                List<String> requiredRoles = List.of(pathRoleConfig.getRole().split(","));
                return userRoles.containsAll(requiredRoles);
            }
        }
        return false;
    }
}