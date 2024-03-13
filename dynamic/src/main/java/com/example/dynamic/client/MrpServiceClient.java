package com.example.dynamic.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dynamic.dto.response.FormFieldResponse;

@FeignClient(name = "mrp-service", url = "http://localhost:8003")
public interface MrpServiceClient {

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getMrpExistingFields(@PathVariable String formName) throws ClassNotFoundException;

	@GetMapping("/getMrpListOfFieldNameValues")
	public List<Object> getMrpListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName);
}
