package com.example.dynamic.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dynamic.dto.response.FormFieldResponse;

@FeignClient(name = "sales-service", url = "${clients.sales.url}")
public interface SalesServiceClient {

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getSalesExistingFields(@PathVariable String formName) throws ClassNotFoundException;

	@GetMapping("/getSalesListOfFieldNameValues")
	public List<Object> getSalesListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName);

}
