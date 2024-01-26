package com.example.plantservice.client.General;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.plantservice.exception.ResourceNotFoundException;

@FeignClient(name = "GENERAL-SERVICE")
public interface MaterialTypeClient {

    @GetMapping("/getMaterialById/{id}")
    MaterialTypeResponse getMaterialById(@PathVariable Long id) throws ResourceNotFoundException;
}