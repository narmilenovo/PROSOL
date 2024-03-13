package com.example.dynamic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamic.dto.request.FormFieldRequest;
import com.example.dynamic.dto.response.FormFieldResponse;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.service.interfaces.FormFieldService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FormFieldController {

	private final FormFieldService fieldService;

	@PostMapping("/saveDynamicField/{formName}")
	public ResponseEntity<Object> saveDynamicField(@PathVariable String formName,
			@RequestBody FormFieldRequest fieldRequest) throws ResourceFoundException {
		FormFieldResponse savedField = fieldService.createDynamicField(formName, fieldRequest);
		return ResponseEntity.ok(savedField);
	}

	@GetMapping("/getDynamicFieldById/{id}")
	public ResponseEntity<Object> getDynamicFieldById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		FormFieldResponse field = fieldService.getDynamicFieldById(id);
		return ResponseEntity.ok(field);
	}

	@GetMapping("/getAllDynamicFieldsByForm/{formName}")
	public ResponseEntity<Object> getAllDynamicFieldsByForm(@PathVariable String formName) {
		List<FormFieldResponse> response = fieldService.getAllDynamicFieldsByForm(formName);
		if (response != null && !response.isEmpty()) {
			return ResponseEntity.ok(response);
		} else {
			return new ResponseEntity<>("No Fields Found with the associate formName : '" + formName + "'",
					HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping("/checkFieldNameInForm")
	public ResponseEntity<Object> checkFieldNameInForm(@RequestParam String fieldName, @RequestParam String formName) {
		boolean exists = fieldService.checkFieldInForm(fieldName, formName);
		return ResponseEntity.ok(exists);
	}

	@PutMapping("/updateDynamicFieldById/{id}")
	public ResponseEntity<Object> updateDynamicFieldById(@PathVariable @NonNull Long id,
			@RequestBody FormFieldRequest updateFieldRequest) throws ResourceNotFoundException, ResourceFoundException {
		FormFieldResponse field = fieldService.updateDynamicFieldById(id, updateFieldRequest);
		return ResponseEntity.ok(field);
	}

	@DeleteMapping("/{id}/removeDynamicField")
	public ResponseEntity<String> removeDynamicFieldById(@PathVariable @NonNull Long id)
			throws ResourceNotFoundException {
		fieldService.removeDynamicFieldById(id);
		return ResponseEntity.noContent().build();
	}

}
