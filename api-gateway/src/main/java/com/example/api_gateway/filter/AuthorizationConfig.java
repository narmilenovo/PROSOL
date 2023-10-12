package com.example.api_gateway.filter;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class AuthorizationConfig {
    private final List<PathRoleConfig> pathRoleConfigList = new ArrayList<>();

    public AuthorizationConfig() {
        // Define path-to-role mappings here
        pathRoleConfigList.add(new PathRoleConfig("/getAllUom", "ROLE_Super"));
        pathRoleConfigList.add(new PathRoleConfig("/getAllUsers", "ROLE_Admin"));
    }

}
