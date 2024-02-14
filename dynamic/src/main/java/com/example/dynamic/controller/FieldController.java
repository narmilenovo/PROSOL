package com.example.dynamic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamic.dto.request.FieldRequest;
import com.example.dynamic.dto.response.FieldResponse;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.service.interfaces.FieldService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FieldController {

	private final FieldService fieldService;

	@PostMapping("/saveField/{formName}")
	public ResponseEntity<Object> createField(@PathVariable String formName, @RequestBody FieldRequest fieldRequest)
			throws ResourceFoundException {
		FieldResponse savedField = fieldService.createField(formName, fieldRequest);
		return ResponseEntity.ok(savedField);
	}

	@GetMapping("/getFieldById/{id}")
	public ResponseEntity<Object> getFieldById(@PathVariable Long id) throws ResourceNotFoundException {
		FieldResponse field = fieldService.getFieldById(id);
		return ResponseEntity.ok(field);
	}

	@GetMapping("/getAllFieldsByForm/{formName}")
	public ResponseEntity<Object> getAllFieldsByForm(@PathVariable String formName) {
		List<FieldResponse> response = fieldService.getAllFieldsByForm(formName);
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

	@PutMapping("/updateFieldById/{id}")
	public ResponseEntity<Object> updateFieldById(@PathVariable Long id, @RequestBody FieldRequest updateFieldRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		FieldResponse field = fieldService.updateFieldById(id, updateFieldRequest);
		return ResponseEntity.ok(field);
	}

	@DeleteMapping("/{id}/removeField")
	public ResponseEntity<String> removeFieldById(@PathVariable Long id) throws ResourceNotFoundException {
		fieldService.removeFieldById(id);
		return ResponseEntity.noContent().build();
	}

}
