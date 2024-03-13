package com.example.dynamic.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dynamic.dto.response.FormFieldResponse;

@FeignClient(name = "plant-service", url = "http://localhost:8001")
public interface PlantServiceClient {

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getPlantExistingFields(@PathVariable String formName) throws ClassNotFoundException;

	@GetMapping("/getPlantListOfFieldNameValues")
	public List<Object> getPlantListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName);
}
