package com.example.valueservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.valueservice.dto.response.FormFieldResponse;
import com.example.valueservice.service.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getValueExistingFields(@PathVariable String formName) throws ClassNotFoundException {
		return commonService.getValueExistingFields(formName);

	}

	@GetMapping("/getValueListOfFieldNameValues")
	public List<Object> getValueListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName) {
		return commonService.getValueListOfFieldNameValues(displayName, formName);
	}

}
