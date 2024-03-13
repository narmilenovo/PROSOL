package com.example.dynamic.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dynamic.dto.response.FormFieldResponse;

@FeignClient(name = "vendor-service", url = "http://localhost:8005")
public interface VendorServiceClient {
	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getVendorExistingFields(@PathVariable String formName) throws ClassNotFoundException;

	@GetMapping("/getVendorListOfFieldNameValues")
	public List<Object> getVendorListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName);
}
