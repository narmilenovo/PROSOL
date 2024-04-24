package com.example.generalservice.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.InspectionCodeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InspectionCodeController {
	private final InspectionCodeService inspectionCodeService;

	@PostMapping("/saveInCode")
	public ResponseEntity<Object> saveInCode(@Valid @RequestBody InspectionCodeRequest inspectionCodeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveInCode").toUriString());
		InspectionCodeResponse inCode = inspectionCodeService.saveInCode(inspectionCodeRequest);
		return ResponseEntity.created(uri).body(inCode);
	}

	@GetMapping("/getInCodeById/{id}")
	public ResponseEntity<Object> getInCodeById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		InspectionCodeResponse codeResponse = inspectionCodeService.getInCodeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(codeResponse);
	}

	@GetMapping("/getAllInCode")
	public ResponseEntity<Object> getAllInCode() {
		List<InspectionCodeResponse> codeResponses = inspectionCodeService.getAllInCode();
		return ResponseEntity.ok(codeResponses);
	}

	@GetMapping("/getAllInCodeTrue")
	public ResponseEntity<Object> listInCodeStatusTrue() {
		List<InspectionCodeResponse> codeResponses = inspectionCodeService.findAllStatusTrue();
		return ResponseEntity.ok(codeResponses);
	}

	@PutMapping("/updateInCode/{id}")
	public ResponseEntity<Object> updateInCode(@PathVariable @NonNull Long id,
			@Valid @RequestBody InspectionCodeRequest updateInspectionCodeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		InspectionCodeResponse codeResponse = inspectionCodeService.updateInCode(id, updateInspectionCodeRequest);
		return ResponseEntity.ok(codeResponse);
	}

	@PatchMapping("/updateInCodeStatus/{id}")
	public ResponseEntity<Object> updateInCodeStatus(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		InspectionCodeResponse codeResponse = inspectionCodeService.updateInCodeStatus(id);
		return ResponseEntity.ok(codeResponse);
	}

	@PatchMapping("/updateBatchInCodeStatus")
	public ResponseEntity<Object> updateBatchInCodeStatus(@RequestBody @NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<InspectionCodeResponse> codeResponses = inspectionCodeService.updateBatchInCodeStatus(ids);
		return ResponseEntity.ok(codeResponses);
	}

	@DeleteMapping("/deleteInCode/{id}")
	public ResponseEntity<Object> deleteInCode(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		inspectionCodeService.deleteInCodeId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchInCode")
	public ResponseEntity<Object> deleteBatchInCode(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		inspectionCodeService.deleteBatchInCode(ids);
		return ResponseEntity.noContent().build();
	}
}
