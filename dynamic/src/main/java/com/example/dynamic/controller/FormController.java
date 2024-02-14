package com.example.dynamic.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.dynamic.dto.request.FormRequest;
import com.example.dynamic.dto.response.FormResponse;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.service.interfaces.FormService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FormController {

	private final FormService formService;

	@PostMapping("/createForm")
	public ResponseEntity<Object> createForm(@RequestBody FormRequest formRequest) {
		FormResponse formResponse = formService.createForm(formRequest);
		return new ResponseEntity<>(formResponse, HttpStatus.CREATED);
	}

	@GetMapping("/getFormById/{id}")
	public ResponseEntity<Object> getFormById(@PathVariable Long id) throws ResourceNotFoundException {
		FormResponse form = formService.getFormById(id);
		return ResponseEntity.ok(form);
	}

	@GetMapping("/getFormByName/{formName}")
	public ResponseEntity<Object> getFormByName(@PathVariable String formName) throws ResourceNotFoundException {
		FormResponse form = formService.getFormByName(formName);
		return ResponseEntity.ok(form);
	}

	@GetMapping("/getAllForm")
	public ResponseEntity<Object> getAllForm() {
		List<FormResponse> response = formService.getAllForm();
		if (!response.isEmpty()) {
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("No Forms Found !!", HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/deleteFormById{id}")
	public ResponseEntity<String> deleteFormById(@PathVariable("id") Long id) throws ResourceNotFoundException {
		formService.deleteFormById(id);
		return new ResponseEntity<>("Form of '" + id + "' is deleted Successfully", HttpStatus.NO_CONTENT);
	}

}
