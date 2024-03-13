package com.example.attributemaster.client.Dynamic;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dynamic-service", url = "http://localhost:8014")
public interface DynamicServiceClient {

	@GetMapping("/checkFieldNameInForm")
	boolean checkFieldNameInForm(@RequestParam String fieldName, @RequestParam String formName);
}