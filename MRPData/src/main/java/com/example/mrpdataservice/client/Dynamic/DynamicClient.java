package com.example.mrpdataservice.client.Dynamic;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "dynamic-service", url = "${clients.dynamic.url}")
public interface DynamicClient {

	@GetMapping("/checkFieldNameInForm")
	boolean checkFieldNameInForm(@RequestParam String fieldName, @RequestParam String formName);
}
