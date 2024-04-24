package com.example.sales_otherservice.controller;

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

import com.example.sales_otherservice.dto.request.TaxClassificationTypeRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationTypeResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.TaxClassificationTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TaxClassificationTypeController {
	private final TaxClassificationTypeService taxClassificationTypeService;

	@PostMapping("/saveTct")
	public ResponseEntity<Object> saveTct(
			@Valid @RequestBody TaxClassificationTypeRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveTct").toUriString());
		TaxClassificationTypeResponse saveTct = taxClassificationTypeService.saveTct(taxClassificationClassRequest);
		return ResponseEntity.created(uri).body(saveTct);
	}

	@GetMapping("/getTctById/{id}")
	public ResponseEntity<Object> getTctById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		TaxClassificationTypeResponse dpById = taxClassificationTypeService.getTctById(id);
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllTct")
	public ResponseEntity<Object> getAllTct() {
		List<TaxClassificationTypeResponse> allTct = taxClassificationTypeService.getAllTct();
		return ResponseEntity.ok(allTct);
	}

	@GetMapping("/getAllTctTrue")
	public ResponseEntity<Object> listTctStatusTrue() {
		List<TaxClassificationTypeResponse> classResponseList = taxClassificationTypeService.findAllStatusTrue();
		return ResponseEntity.ok(classResponseList);
	}

	@PutMapping("/updateTct/{id}")
	public ResponseEntity<Object> updateTct(@PathVariable @NonNull Long id,
			@Valid @RequestBody TaxClassificationTypeRequest updateTaxClassificationTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		TaxClassificationTypeResponse updateTct = taxClassificationTypeService.updateTct(id,
				updateTaxClassificationTypeRequest);
		return ResponseEntity.ok(updateTct);
	}

	@PatchMapping("/updateTctStatus/{id}")
	public ResponseEntity<Object> updateTctStatus(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		TaxClassificationTypeResponse tctResponse = taxClassificationTypeService.updateTctStatus(id);
		return ResponseEntity.ok(tctResponse);
	}

	@PatchMapping("/updateBatchTctStatus")
	public ResponseEntity<Object> updateBatchTctStatus(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		List<TaxClassificationTypeResponse> tctResponses = taxClassificationTypeService.updateBatchTctStatus(ids);
		return ResponseEntity.ok(tctResponses);
	}

	@DeleteMapping("/deleteTct/{id}")
	public ResponseEntity<Object> deleteTct(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		taxClassificationTypeService.deleteTctById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchTct")
	public ResponseEntity<Object> deleteBatchTct(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		taxClassificationTypeService.deleteBatchTct(ids);
		return ResponseEntity.noContent().build();
	}
}
