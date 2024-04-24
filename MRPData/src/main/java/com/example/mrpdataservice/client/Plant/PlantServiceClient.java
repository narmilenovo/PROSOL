package com.example.mrpdataservice.client.Plant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.mrpdataservice.exception.ResourceNotFoundException;

@FeignClient(name = "plant-service", url = "${clients.plant.url}")
public interface PlantServiceClient {

	@GetMapping("/getPlantById/{plantId}")
	PlantResponse getPlantById(@PathVariable Long plantId) throws ResourceNotFoundException;

}
