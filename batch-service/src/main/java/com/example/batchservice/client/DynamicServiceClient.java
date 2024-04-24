package com.example.batchservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "dynamic-service", url = "http://localhost:8014")
public interface DynamicServiceClient {

	@GetMapping("/getDynamicFieldsListInForm/{formName}")
	public List<String> getDynamicFieldsListInForm(@PathVariable String formName);

}
