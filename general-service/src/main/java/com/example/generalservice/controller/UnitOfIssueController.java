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

import com.example.generalservice.dto.request.UnitOfIssueRequest;
import com.example.generalservice.dto.response.UnitOfIssueResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.UnitOfIssueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UnitOfIssueController {
	private final UnitOfIssueService unitOfIssueService;

	@PostMapping("/saveUOI")
	public ResponseEntity<Object> saveUOI(@Valid @RequestBody UnitOfIssueRequest unitOfIssueRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveUOI").toUriString());
		UnitOfIssueResponse unitOfIssueResponse = unitOfIssueService.saveUOI(unitOfIssueRequest);
		return ResponseEntity.created(uri).body(unitOfIssueResponse);
	}

	@GetMapping("/getUOIById/{id}")
	public ResponseEntity<Object> getUOIById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		UnitOfIssueResponse unitOfIssueResponse = unitOfIssueService.getUOIById(id);
		return ResponseEntity.status(HttpStatus.OK).body(unitOfIssueResponse);
	}

	@GetMapping("/getAllUOI")
	public ResponseEntity<Object> getAllUOI() {
		List<UnitOfIssueResponse> unitOfIssueResponses = unitOfIssueService.getAllUOI();
		return ResponseEntity.ok(unitOfIssueResponses);
	}

	@GetMapping("/getAllUOITrue")
	public ResponseEntity<Object> listUOIStatusTrue() {
		List<UnitOfIssueResponse> unitOfIssueResponses = unitOfIssueService.findAllStatusTrue();
		return ResponseEntity.ok(unitOfIssueResponses);
	}

	@PutMapping("/updateUOI/{id}")
	public ResponseEntity<Object> updateUOI(@PathVariable @NonNull Long id,
			@Valid @RequestBody UnitOfIssueRequest updateUnitOfIssueRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		UnitOfIssueResponse unitOfIssueResponse = unitOfIssueService.updateUOI(id, updateUnitOfIssueRequest);
		return ResponseEntity.ok(unitOfIssueResponse);
	}

	@PatchMapping("/updateUOIStatus/{id}")
	public ResponseEntity<Object> updateUOIStatus(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		UnitOfIssueResponse unitOfIssueResponse = unitOfIssueService.updateUOIStatus(id);
		return ResponseEntity.ok(unitOfIssueResponse);
	}

	@PatchMapping("/updateBatchUOIStatus")
	public ResponseEntity<Object> updateBatchUOIStatus(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		List<UnitOfIssueResponse> unitOfIssueResponses = unitOfIssueService.updateBatchUOIStatus(ids);
		return ResponseEntity.ok(unitOfIssueResponses);
	}

	@DeleteMapping("/deleteUOI/{id}")
	public ResponseEntity<Object> deleteUOI(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		unitOfIssueService.deleteUOIId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchUOI")
	public ResponseEntity<Object> deleteBatchUOI(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		unitOfIssueService.deleteBatchUOI(ids);
		return ResponseEntity.noContent().build();
	}
}
