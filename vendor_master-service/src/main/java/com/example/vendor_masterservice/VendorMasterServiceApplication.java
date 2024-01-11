package com.example.vendor_masterservice;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.example.vendor_masterservice.configuration.SpringSecurityAuditorAware;

import jakarta.servlet.http.HttpServletRequest;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class VendorMasterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VendorMasterServiceApplication.class, args);
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
