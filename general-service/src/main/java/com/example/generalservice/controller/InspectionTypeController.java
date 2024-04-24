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

import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.InspectionTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class InspectionTypeController {
	private final InspectionTypeService inspectionTypeService;

	@PostMapping("/saveInType")
	public ResponseEntity<Object> saveInCode(@Valid @RequestBody InspectionTypeRequest inspectionTypeRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveInType").toUriString());
		InspectionTypeResponse inCode = inspectionTypeService.saveInType(inspectionTypeRequest);
		return ResponseEntity.created(uri).body(inCode);
	}

	@GetMapping("/getInTypeById/{id}")
	public ResponseEntity<Object> getInTypeById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		InspectionTypeResponse codeResponse = inspectionTypeService.getInTypeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(codeResponse);
	}

	@GetMapping("/getAllInType")
	public ResponseEntity<Object> getAllInType() {
		List<InspectionTypeResponse> codeResponses = inspectionTypeService.getAllInType();
		return ResponseEntity.ok(codeResponses);
	}

	@GetMapping("/getAllInTypeTrue")
	public ResponseEntity<Object> listInTypeStatusTrue() {
		List<InspectionTypeResponse> codeResponses = inspectionTypeService.findAllStatusTrue();
		return ResponseEntity.ok(codeResponses);
	}

	@PutMapping("/updateInType/{id}")
	public ResponseEntity<Object> updateInType(@PathVariable @NonNull Long id,
			@Valid @RequestBody InspectionTypeRequest updateInspectionTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		InspectionTypeResponse codeResponse = inspectionTypeService.updateInType(id, updateInspectionTypeRequest);
		return ResponseEntity.ok(codeResponse);
	}

	@PatchMapping("/updateInTypeStatus/{id}")
	public ResponseEntity<Object> updateInTypeStatus(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		InspectionTypeResponse codeResponse = inspectionTypeService.updateInTypeStatus(id);
		return ResponseEntity.ok(codeResponse);
	}

	@PatchMapping("/updateBatchInTypeStatus")
	public ResponseEntity<Object> updateBatchInTypeStatus(@RequestBody @NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<InspectionTypeResponse> codeResponses = inspectionTypeService.updateBatchInTypeStatus(ids);
		return ResponseEntity.ok(codeResponses);
	}

	@DeleteMapping("/deleteInType/{id}")
	public ResponseEntity<Object> deleteInCode(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		inspectionTypeService.deleteInTypeId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchInType")
	public ResponseEntity<Object> deleteBatchInType(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		inspectionTypeService.deleteBatchInType(ids);
		return ResponseEntity.noContent().build();
	}
}
