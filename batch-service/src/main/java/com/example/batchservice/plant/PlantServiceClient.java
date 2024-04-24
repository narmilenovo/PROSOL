package com.example.batchservice.plant;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.batchservice.request.PlantRequest;

import jakarta.validation.Valid;

@FeignClient(name = "plant-service", url = "http://localhost:8001")
public interface PlantServiceClient {

	@PostMapping("/saveAllPlant")
	public void saveAllPlant(@Valid @RequestBody List<PlantRequest> plantRequests);

}
