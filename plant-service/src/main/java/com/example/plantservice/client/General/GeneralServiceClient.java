package com.example.plantservice.client.General;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.plantservice.exception.ResourceNotFoundException;

@FeignClient(name = "general-service", url = "http://localhost:8002")
public interface GeneralServiceClient {

	@GetMapping("/getMaterialById/{id}")
	MaterialTypeResponse getMaterialById(@PathVariable Long id) throws ResourceNotFoundException;
}