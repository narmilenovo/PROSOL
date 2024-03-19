package com.example.api_gateway.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.config.GlobalCorsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import com.example.api_gateway.security.AuthenticationManager;
import com.example.api_gateway.security.SecurityContextRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	protected static final List<String> SWAGGER = List.of("/v3/api-docs/**", "/swagger-resources/**",
			"/configuration/ui/**", "/configuration/security/**", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html");

	protected static final List<String> USER = List.of("/user/auth/**", "/user/v3/api-docs", "/actuator/**",
			"user/forgotPassword", "/user/resetPassword**"

	);
	protected static final List<String> PERMIT_ALL_URLS = new ArrayList<>();
	private static final List<String> GENERAL = List.of("/general/v3/api-docs");
	private static final List<String> SALES = List.of("/sales/v3/api-docs");
	private static final List<String> PLANT = List.of("/plant/v3/api-docs");
	private static final List<String> MRP = List.of("/mrp/v3/api-docs");
	private static final List<String> VENDOR = List.of("/vendor/v3/api-docs");
	private static final List<String> SETTINGS = List.of("/setting/v3/api-docs");
	private static final List<String> VALUE = List.of("/value/v3/api-docs");
	private static final List<String> ATTRIBUTE = List.of("/attribute/v3/api-docs");
	private static final List<String> DICTIONARY = List.of("/dictionary/v3/api-docs", "dictionary/downloadFile/**");
	private static final List<String> REQUEST = List.of("/request/v3/api-docs");
	private static final List<String> DYNAMIC = List.of("/dynamic/v3/api-docs");
	private static final List<String> USER_SETTINGS = List.of("/userSettings/v3/api-docs");
	private static final List<String> BATCH_SERVICE = List.of("/batch/v3/api-docs");

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
		PERMIT_ALL_URLS.addAll(ATTRIBUTE);
		PERMIT_ALL_URLS.addAll(DICTIONARY);
		PERMIT_ALL_URLS.addAll(REQUEST);
		PERMIT_ALL_URLS.addAll(DYNAMIC);
		PERMIT_ALL_URLS.addAll(USER_SETTINGS);
		PERMIT_ALL_URLS.addAll(BATCH_SERVICE);
	}

	private final AuthenticationManager authenticationManager;
	private final SecurityContextRepository securityContextRepository;

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http.exceptionHandling(exc -> exc
				.authenticationEntryPoint(
						(swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
				.accessDeniedHandler(
						(swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN))))
				.csrf(ServerHttpSecurity.CsrfSpec::disable).formLogin(ServerHttpSecurity.FormLoginSpec::disable)
				.cors(Customizer.withDefaults()).httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
				.authenticationManager(authenticationManager).securityContextRepository(securityContextRepository)
				.authorizeExchange(exchange -> exchange.pathMatchers(PERMIT_ALL_URLS.toArray(new String[0])).permitAll()
						.pathMatchers("/user/getAllUsers").hasRole("Admin").anyExchange().authenticated());
		return http.build();
	}

	@Bean
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@RefreshScope
	CorsWebFilter corsWebFilter(GlobalCorsProperties properties) {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		properties.getCorsConfigurations().forEach(source::registerCorsConfiguration);
		return new CorsWebFilter(source);
	}
}