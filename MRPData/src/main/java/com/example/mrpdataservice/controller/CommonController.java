package com.example.mrpdataservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.mrpdataservice.response.FormFieldResponse;
import com.example.mrpdataservice.serviceimpl.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getMrpExistingFields(@PathVariable String formName) throws ClassNotFoundException {
		return commonService.getMrpExistingFields(formName);
	}

	@GetMapping("/getMrpListOfFieldNameValues")
	public List<Object> getMrpListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName) {
		return commonService.getMrpListOfFieldNameValues(displayName, formName);
	}
}
