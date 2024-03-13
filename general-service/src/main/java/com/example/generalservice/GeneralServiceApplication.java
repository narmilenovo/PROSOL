package com.example.generalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.generalservice.configuration.SpringSecurityAuditorAware;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableFeignClients
public class GeneralServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeneralServiceApplication.class, args);
	}

	@Bean
	AuditorAware<String> auditorAware(HttpServletRequest request) {
		return new SpringSecurityAuditorAware(request);
	}
}
