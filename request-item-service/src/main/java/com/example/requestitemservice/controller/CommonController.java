package com.example.requestitemservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.requestitemservice.dto.response.FormFieldResponse;
import com.example.requestitemservice.service.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getRequestItemExistingFields(@PathVariable String formName)
			throws ClassNotFoundException {
		return commonService.getRequestItemExistingFields(formName);

	}

	@GetMapping("/getRequestItemListOfFieldNameValues")
	public List<Object> getRequestItemListOfFieldNameValues(@RequestParam String displayName,
			@RequestParam String formName) {
		return commonService.getRequestItemListOfFieldNameValues(displayName, formName);
	}

}
