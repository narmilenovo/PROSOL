package com.example.user_management;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.user_management.configuration.SpringSecurityAuditorAware;
import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.service.interfaces.PrivilegeService;
import com.example.user_management.service.interfaces.RoleService;
import com.example.user_management.service.interfaces.UserService;

@SpringBootApplication(exclude = { ThymeleafAutoConfiguration.class })
@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class UserManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserManagementApplication.class, args);
	}

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuditorAware<String> auditorAware() {
		return new SpringSecurityAuditorAware();
	}

	@Bean
	CommandLineRunner runner(UserService userService, RoleService roleService, PrivilegeService privilegeService) {
		return args -> {
			privilegeService.savePrivilege(new PrivilegeRequest("create", true));
			privilegeService.savePrivilege(new PrivilegeRequest("read", true));
			privilegeService.savePrivilege(new PrivilegeRequest("update", true));
			privilegeService.savePrivilege(new PrivilegeRequest("delete", true));

			roleService.saveRole(new RoleRequest("user", "demo", 1L, true, new Long[] { 1L, 2L }));
			roleService.saveRole(new RoleRequest("admin", "demo", 1L, true, new Long[] { 1L, 2L, 3L, 4L }));
			roleService.saveRole(new RoleRequest("super", "demo", 1L, true, new Long[] { 1L, 2L, 3L }));

			userService.saveUser(new UserRequest("imran@gmail.com", "Zz12345", "Zz12345", "ne", "mo", "7143478749",
					"dede", 1L, List.of(1L), true, new Long[] { 1L }));
			userService.saveUser(new UserRequest("trialforall2022@gmail.com", "Zz12345", "Zz12345", "tri", "all",
					"7143478749", "true", 1L, List.of(1L, 2L), true, new Long[] { 2L }));
			userService.saveUser(new UserRequest("nemo@gmail.com", "Zz12345", "Zz12345", "md", "nemi", "7143478749",
					"true", 1L, List.of(2L), true, new Long[] { 3L }));
		};
	}

}
