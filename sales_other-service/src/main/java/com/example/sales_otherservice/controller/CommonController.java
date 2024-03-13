package com.example.sales_otherservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.sales_otherservice.dto.response.FormFieldResponse;
import com.example.sales_otherservice.service.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getSalesExistingFields(@PathVariable String formName) throws ClassNotFoundException {
		return commonService.getSalesExistingFields(formName);
	}

	@GetMapping("/getSalesListOfFieldNameValues")
	public List<Object> getSalesListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName) {
		return commonService.getSalesListOfFieldNameValues(displayName, formName);
	}

}
