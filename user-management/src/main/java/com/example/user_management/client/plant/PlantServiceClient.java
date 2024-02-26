package com.example.user_management.client.plant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "plant-service", url = "http://localhost:8001")
public interface PlantServiceClient {

	@GetMapping("/getDepartmentById/{id}")
	DepartmentResponse getDepartmentById(@PathVariable Long id);

	@GetMapping("/getPlantById/{plantId}")
	PlantResponse getPlantById(@PathVariable Long plantId);
}
