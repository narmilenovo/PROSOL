package com.example.dynamic.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dynamic.dto.response.FormFieldResponse;

@FeignClient(name = "general-service", url = "http://localhost:8002")
public interface GeneralServiceClient {
	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getGeneralServiceExistingFields(@PathVariable String formName)
			throws ClassNotFoundException;

	@GetMapping("/getGeneralServiceListOfFieldNameValues")
	public List<Object> getGeneralServiceListOfFieldNameValues(@RequestParam String displayName,
			@RequestParam String formName);
}
