package com.example.vendor_masterservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.vendor_masterservice.dto.response.FormFieldResponse;
import com.example.vendor_masterservice.service.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getVendorExistingFields(@PathVariable String formName)
			throws ClassNotFoundException {
		return commonService.getVendorExistingFields(formName);

	}

	@GetMapping("/getVendorListOfFieldNameValues")
	public List<Object> getVendorListOfFieldNameValues(@RequestParam String displayName,
			@RequestParam String formName) {
		return commonService.getVendorListOfFieldNameValues(displayName, formName);
	}

}
