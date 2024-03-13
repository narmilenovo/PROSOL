package com.example.plantservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.plantservice.dto.response.FormFieldResponse;
import com.example.plantservice.service.CommonService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommonController {

	private final CommonService commonService;

	@GetMapping("/fields/{formName}")
	public List<FormFieldResponse> getPlantExistingFields(@PathVariable String formName) throws ClassNotFoundException {
		return commonService.getPlantExistingFields(formName);

	}

	@GetMapping("/getPlantListOfFieldNameValues")
	public List<Object> getPlantListOfFieldNameValues(@RequestParam String displayName, @RequestParam String formName) {
		return commonService.getPlantListOfFieldNameValues(displayName, formName);
	}

}
