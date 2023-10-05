package com.example.api_gateway.config;


import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        if (definitions != null) { // Check if definitions is null
            for (RouteDefinition definition : definitions) {
                System.out.println("id: " + definition.getId() + "  " + definition.getUri().toString());
            }

            definitions.stream().filter(routeDefinition -> routeDefinition.getId().matches(".*-SERVICE")).forEach(routeDefinition -> {
                String name = routeDefinition.getId().replace("-SERVICE", ""); // Replace with "replace()" method
                GroupedOpenApi.builder().pathsToMatch("/" + name + "/**").group(name).build();
            });
        }

        return groups;
    }


}