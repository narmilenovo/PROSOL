package com.example.generalsettings;

import com.example.generalsettings.config.SpringSecurityAuditorAware;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class GeneralsettingsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GeneralsettingsApplication.class, args);
	}

    @Bean
    ModelMapper modelMapper() {
		return new ModelMapper();
	}

    @Bean
    AuditorAware<String> auditorAware(HttpServletRequest request) {
		return new SpringSecurityAuditorAware(request);
	}
}
