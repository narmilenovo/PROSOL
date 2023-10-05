package com.example.service_registry.utils;

import com.example.service_registry.dto.AuthenticationResponse;
import com.example.service_registry.dto.LoginRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "USER-SERVICE", url = "http://localhost:8000/user")
public interface UserManagementClient {
    @PostMapping("/auth/login")
    AuthenticationResponse login(@RequestBody LoginRequest loginRequest);
}
