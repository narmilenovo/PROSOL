package com.example.generalservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.generalservice.dto.response.FormFieldResponse;
import com.example.generalservice.service.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getGeneralServiceExistingFields(@PathVariable String formName)
			throws ClassNotFoundException {
		return commonService.getGeneralServiceExistingFields(formName);
	}

	@GetMapping("/getGeneralServiceListOfFieldNameValues")
	public List<Object> getGeneralServiceListOfFieldNameValues(@RequestParam String displayName,
			@RequestParam String formName) {
		return commonService.getGeneralServiceListOfFieldNameValues(displayName, formName);
	}

}
