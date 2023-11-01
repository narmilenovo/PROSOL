package com.example.plantservice.config;

import com.example.plantservice.dto.response.MaterialTypeResponse;
import com.example.plantservice.exception.ResourceNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "GENERAL-SERVICE", url = "http://localhost:8001")
public interface MaterialTypeClient {

    @GetMapping("/getMaterialById/{id}")
    MaterialTypeResponse getMaterialById(@PathVariable Long id) throws ResourceNotFoundException;
}