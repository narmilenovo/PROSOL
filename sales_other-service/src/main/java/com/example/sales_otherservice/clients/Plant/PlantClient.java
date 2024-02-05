package com.example.sales_otherservice.clients.Plant;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "plant-service", url = "http://localhost:8001")
public interface PlantClient {

	@GetMapping("/getPlantById/{plantId}")
	PlantResponse getPlantById(@PathVariable Long plantId);

}