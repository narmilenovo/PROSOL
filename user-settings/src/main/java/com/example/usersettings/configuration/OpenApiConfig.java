package com.example.usersettings.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@OpenAPIDefinition
@Configuration
public class OpenApiConfig {

	@Bean
	OpenAPI customOpenAPI(@Value("${springdoc.service.title}") String serviceTitle,
			@Value("${springdoc.service.version}") String serviceVersion) {
		final String securitySchemeName = "bearerAuth";
		return new OpenAPI()
				.components(new Components().addSecuritySchemes(securitySchemeName,
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.security(List.of(new SecurityRequirement().addList(securitySchemeName)))
				.info(new Info().title(serviceTitle).version(serviceVersion));
	}
}