package com.example.api_gateway.config;

import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancedExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	private final LoadBalancedExchangeFilterFunction filterFunction;

	@Bean
	WebClient userWebClientClient() {
		return WebClient.builder().baseUrl("http://USER-SERVICE").filter(filterFunction).build();
	}

}
