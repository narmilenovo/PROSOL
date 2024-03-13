package com.example.attributemaster.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.attributemaster.response.FormFieldResponse;
import com.example.attributemaster.service.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getAttributeExistingFields(@PathVariable String formName)
			throws ClassNotFoundException {
		return commonService.getAttributeExistingFields(formName);

	}

	@GetMapping("/getAttributeListOfFieldNameValues")
	public List<Object> getAttributeListOfFieldNameValues(@RequestParam String displayName,
			@RequestParam String formName) {
		return commonService.getAttributeListOfFieldNameValues(displayName, formName);
	}

}
