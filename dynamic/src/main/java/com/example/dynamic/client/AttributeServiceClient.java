package com.example.dynamic.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dynamic.dto.response.FormFieldResponse;

@FeignClient(name = "attribute-service", url = "http://localhost:8010")
public interface AttributeServiceClient {

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getAttributeExistingFields(@PathVariable String formName)
			throws ClassNotFoundException;

	@GetMapping("/getAttributeListOfFieldNameValues")
	public List<Object> getAttributeListOfFieldNameValues(@RequestParam String displayName,
			@RequestParam String formName);
}
