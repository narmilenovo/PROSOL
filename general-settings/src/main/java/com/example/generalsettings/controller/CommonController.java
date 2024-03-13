package com.example.generalsettings.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.generalsettings.response.FormFieldResponse;
import com.example.generalsettings.serviceimpl.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getGeneralSettingsExistingFields(@PathVariable String formName)
			throws ClassNotFoundException {
		return commonService.getGeneralSettingsExistingFields(formName);
	}

	@GetMapping("/getGeneralSettingsListOfFieldNameValues")
	public List<Object> getGeneralSettingsListOfFieldNameValues(@RequestParam String displayName,
			@RequestParam String formName) {
		return commonService.getGeneralSettingsListOfFieldNameValues(displayName, formName);
	}

}
