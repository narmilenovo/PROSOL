package com.example.generalservice.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.generalservice.dto.request.SalesUnitRequest;
import com.example.generalservice.dto.response.SalesUnitResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.SalesUnitService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SalesUnitController {
	private final SalesUnitService salesUnitService;

	@PostMapping("/saveSalesUnit")
	public ResponseEntity<Object> saveSalesUnit(@Valid @RequestBody SalesUnitRequest salesUnitRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveUom").toUriString());
		SalesUnitResponse saveSalesUnit = salesUnitService.saveSalesUnit(salesUnitRequest);
		return ResponseEntity.created(uri).body(saveSalesUnit);
	}

	@GetMapping("/getSalesUnitById/{id}")
	public ResponseEntity<Object> getSalesUnitById(@PathVariable Long id) throws ResourceNotFoundException {
		SalesUnitResponse salesUnitResponse = salesUnitService.getSalesUnitById(id);
		return ResponseEntity.status(HttpStatus.OK).body(salesUnitResponse);
	}

	@GetMapping("/getAllSalesUnit")
	public ResponseEntity<Object> getAllSalesUnit() {
		List<SalesUnitResponse> salesUnit = salesUnitService.getAllSalesUnit();
		return ResponseEntity.ok(salesUnit);
	}

	@GetMapping("/getAllSalesUnitTrue")
	public ResponseEntity<Object> listSalesUnitStatusTrue() {
		List<SalesUnitResponse> salesUnitResponses = salesUnitService.findAllStatusTrue();
		return ResponseEntity.ok(salesUnitResponses);
	}

	@PutMapping("/updateSalesUnit/{id}")
	public ResponseEntity<Object> updateSalesUnit(@PathVariable Long id,
			@Valid @RequestBody SalesUnitRequest updateSalesUnitRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		SalesUnitResponse updateSalesUnit = salesUnitService.updateSalesUnit(id, updateSalesUnitRequest);
		return ResponseEntity.ok(updateSalesUnit);
	}

	@PatchMapping("/updateSalesUnitStatus/{id}")
	public ResponseEntity<Object> updateSalesUnitStatus(@PathVariable Long id) throws ResourceNotFoundException {
		SalesUnitResponse updateSalesUnit = salesUnitService.updateSalesUnitStatus(id);
		return ResponseEntity.ok(updateSalesUnit);
	}

	@PatchMapping("/updateBatchSalesUnitStatus")
	public ResponseEntity<Object> updateBatchSalesUnitStatus(@RequestBody List<Long> ids) {
		List<SalesUnitResponse> updateSalesUnits = salesUnitService.updateBatchSalesUnitStatus(ids);
		return ResponseEntity.ok(updateSalesUnits);
	}

	@DeleteMapping("/deleteSalesUnit/{id}")
	public ResponseEntity<Object> deleteSalesUnit(@PathVariable Long id) throws ResourceNotFoundException {
		salesUnitService.deleteSalesUnitId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchSalesUnit")
	public ResponseEntity<Object> deleteBatchSalesUnit(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		salesUnitService.deleteBatchSalesUnit(ids);
		return ResponseEntity.noContent().build();
	}
}
