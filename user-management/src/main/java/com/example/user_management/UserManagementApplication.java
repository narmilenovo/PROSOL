package com.example.user_management;

import com.example.user_management.configuration.SpringSecurityAuditorAware;
import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.service.interfaces.PrivilegeService;
import com.example.user_management.service.interfaces.RoleService;
import com.example.user_management.service.interfaces.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(exclude = {ThymeleafAutoConfiguration.class})
@EnableScheduling
@EnableCaching
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class UserManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new SpringSecurityAuditorAware();
    }

    @Bean
    CommandLineRunner runner(UserService userService, RoleService roleService, PrivilegeService privilegeService) {
        return args -> {
            privilegeService.savePrivilege(new PrivilegeRequest("create", true));
            privilegeService.savePrivilege(new PrivilegeRequest("read", true));
            privilegeService.savePrivilege(new PrivilegeRequest("update", true));
            privilegeService.savePrivilege(new PrivilegeRequest("delete", true));

            roleService.saveRole(new RoleRequest("user", "demo", "plant", true, new Long[]{1L, 2L}));
            roleService.saveRole(new RoleRequest("admin", "demo", "plant", true, new Long[]{1L, 2L, 3L, 4L}));
            roleService.saveRole(new RoleRequest("super", "demo", "plant", true, new Long[]{1L, 2L, 3L}));

            userService.saveUser(new UserRequest("imran@gmail.com", "Zz12345", "Zz12345", "ne", "mo", "+917143478749", "dede", 1L, "true", true, new Long[]{1L}));
            userService.saveUser(new UserRequest("trialforall2022@gmail.com", "Zz12345", "Zz12345", "tri", "all", "+917143478749", "true", 2L, "true", true, new Long[]{2L}));
            userService.saveUser(new UserRequest("nemo@gmail.com", "Zz12345", "Zz12345", "md", "nemi", "+917143478749", "true", 3L, "true", true, new Long[]{3L}));
        };
    }

}