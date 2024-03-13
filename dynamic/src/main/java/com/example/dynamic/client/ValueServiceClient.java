package com.example.dynamic.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dynamic.dto.response.FormFieldResponse;

@FeignClient(name = "value-service", url = "http://localhost:8012")
public interface ValueServiceClient {
	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getValueExistingFields(@PathVariable String formName) throws ClassNotFoundException;

	@GetMapping("/getValueListOfFieldNameValues")
	public List<Object> getValueListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName);
}
