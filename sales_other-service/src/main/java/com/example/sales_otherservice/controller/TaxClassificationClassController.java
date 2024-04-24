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

import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.TaxClassificationClassService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TaxClassificationClassController {
	private final TaxClassificationClassService taxClassificationClassService;

	@PostMapping("/saveTcc")
	public ResponseEntity<Object> saveTcc(
			@Valid @RequestBody TaxClassificationClassRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveTcc").toUriString());
		TaxClassificationClassResponse saveTcc = taxClassificationClassService.saveTcc(taxClassificationClassRequest);
		return ResponseEntity.created(uri).body(saveTcc);
	}

	@GetMapping("/getTccById/{id}")
	public ResponseEntity<Object> getTccById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		TaxClassificationClassResponse dpById = taxClassificationClassService.getTccById(id);
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllTccTrue")
	public ResponseEntity<Object> listTccStatusTrue() {
		List<TaxClassificationClassResponse> classResponseList = taxClassificationClassService.findAllStatusTrue();
		return ResponseEntity.ok(classResponseList);
	}

	@GetMapping("/getAllTcc")
	public ResponseEntity<Object> getAllTcc() {
		List<TaxClassificationClassResponse> allTcc = taxClassificationClassService.getAllTcc();
		return ResponseEntity.ok(allTcc);
	}

	@PutMapping("/updateTcc/{id}")
	public ResponseEntity<Object> updateTcc(@PathVariable @NonNull Long id,
			@Valid @RequestBody TaxClassificationClassRequest updateTaxClassificationClassRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		TaxClassificationClassResponse updateTcc = taxClassificationClassService.updateTcc(id,
				updateTaxClassificationClassRequest);
		return ResponseEntity.ok(updateTcc);
	}

	@PatchMapping("/updateTccStatus/{id}")
	public ResponseEntity<Object> updateTccStatus(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		TaxClassificationClassResponse tccResponse = taxClassificationClassService.updateTccStatus(id);
		return ResponseEntity.ok(tccResponse);
	}

	@PatchMapping("/updateBatchTccStatus")
	public ResponseEntity<Object> updateBatchTccStatus(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		List<TaxClassificationClassResponse> tccResponses = taxClassificationClassService.updateBatchTccStatus(ids);
		return ResponseEntity.ok(tccResponses);
	}

	@DeleteMapping("/deleteTcc/{id}")
	public ResponseEntity<Object> deleteTcc(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		taxClassificationClassService.deleteTccById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchTcc")
	public ResponseEntity<Object> deleteBatchTcc(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		taxClassificationClassService.deleteBatchTcc(ids);
		return ResponseEntity.noContent().build();
	}
}
