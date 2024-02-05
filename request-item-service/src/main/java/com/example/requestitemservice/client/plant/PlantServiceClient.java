package com.example.requestitemservice.client.plant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "plant-service", url = "http://localhost:8001")
public interface PlantServiceClient {

	@GetMapping("/getPlantById/{plantId}")
	public PlantResponse getPlantById(@PathVariable Long plantId);

	@GetMapping("/getAllByPlant/{name}")
	public StorageLocationResponse getAllByPlant(@PathVariable String name);

	@GetMapping("/getStorageLocationById/{id}")
	public StorageLocationResponse getStorageLocationById(@PathVariable Long id);

}
