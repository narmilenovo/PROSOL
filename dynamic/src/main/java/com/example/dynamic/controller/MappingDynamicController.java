package com.example.dynamic.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamic.dto.response.FormFieldResponse;
import com.example.dynamic.service.interfaces.FormFieldService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MappingDynamicController {

	private final FormFieldService fieldService;

	@GetMapping("/getDynamicFieldsAndExistingFields/{formName}")
	public ResponseEntity<Object> getDynamicFieldsAndExistingFields(@PathVariable String formName,
			@RequestParam(required = false) Boolean extraFields) throws ClassNotFoundException {
		List<FormFieldResponse> formField = fieldService.getDynamicFieldsAndExistingFields(formName, extraFields);
		return ResponseEntity.ok(formField);
	}

	@GetMapping("/getAllFieldNamesOfForm/{formName}")
	public ResponseEntity<Object> getAllFieldNamesOfForm(@PathVariable String formName) throws ClassNotFoundException {
		List<String> field = fieldService.getAllFieldNamesOfForm(formName);
		return ResponseEntity.ok(field);
	}
	
	@GetMapping("/getDynamicFieldsListInForm/{formName}")
	public ResponseEntity<Object> getDynamicFieldsListInForm(@PathVariable String formName) throws ClassNotFoundException {
		List<String> field = fieldService.getDynamicFieldsListInForm(formName);
		return ResponseEntity.ok(field);
	}

	@GetMapping("/getListOfFieldNameValues")
	public ResponseEntity<Object> getListOfFieldNameValues(@RequestParam String displayName,
			@RequestParam String formName) {
		List<Object> fieldValues = fieldService.getListOfFieldNameValues(displayName, formName);
		return ResponseEntity.ok(fieldValues);
	}
}
