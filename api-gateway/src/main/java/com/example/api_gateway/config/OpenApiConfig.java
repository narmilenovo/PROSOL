package com.example.api_gateway.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@OpenAPIDefinition
@Configuration
@Slf4j
public class OpenApiConfig {

    public static final String BEARER_AUTH = "bearerAuth";
    @Value("${springdoc.service.title}")
    private String serviceTitle;
    @Value("${springdoc.service.version}")
    private String serviceVersion;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(Collections.singletonList(new Server().url("http://localhost:9191")))
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        BEARER_AUTH,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                                .name("Authorization")))
                .security(List.of(new SecurityRequirement().addList(BEARER_AUTH)))
                .info(new Info().title(serviceTitle).version(serviceVersion));


    }


    @Bean
//    @Lazy(false)
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        if (definitions != null) {
            for (RouteDefinition definition : definitions) {
                log.info("id: " + definition.getId() + "  " + definition.getUri().toString());
            }

            // Add a global security requirement for JWT to all services
            SecurityRequirement globalSecurity = new SecurityRequirement().addList(BEARER_AUTH);


            definitions.stream()
                    .filter(routeDefinition -> routeDefinition.getId().matches(".*-SERVICE"))
                    .forEach(routeDefinition -> {
                        String name = routeDefinition.getId().replace("-SERVICE", "");
                        GroupedOpenApi group = GroupedOpenApi.builder()
                                .pathsToMatch("/" + name + "/**")
                                .group(name)
                                .addOperationCustomizer((operation, handlerMethod) -> operation
                                        .security(List.of(globalSecurity))) // Add global security to each operation

                                .build();
                        groups.add(group);

                    });
        }
        return groups;
    }
}