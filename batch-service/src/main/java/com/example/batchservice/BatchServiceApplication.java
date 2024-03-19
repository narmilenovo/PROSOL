package com.example.batchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.batchservice.configuration.SpringSecurityAuditorAware;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class BatchServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchServiceApplication.class, args);
	}

	@Bean
	AuditorAware<String> auditorAware(HttpServletRequest request) {
		return new SpringSecurityAuditorAware(request);
	}
}
