package com.example.sales_otherservice.controller;

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

import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.SalesOrganizationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SalesOrganizationController {
	private final SalesOrganizationService salesOrganizationService;

	@PostMapping("/saveSo")
	public ResponseEntity<Object> saveSo(@Valid @RequestBody SalesOrganizationRequest salesOrganizationRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSo").toUriString());
		SalesOrganizationResponse saveSo = salesOrganizationService.saveSo(salesOrganizationRequest);
		return ResponseEntity.created(uri).body(saveSo);
	}

	@GetMapping("/getSoById/{id}")
	public ResponseEntity<Object> getSoById(@PathVariable Long id) throws ResourceNotFoundException {
		SalesOrganizationResponse dpById = salesOrganizationService.getSoById(id);
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllSo")
	public ResponseEntity<Object> getAllSo() {
		List<SalesOrganizationResponse> allSo = salesOrganizationService.getAllSo();
		return ResponseEntity.ok(allSo);
	}

	@GetMapping("/getAllSoTrue")
	public ResponseEntity<Object> listSoStatusTrue() {
		List<SalesOrganizationResponse> valueKeyResponseList = salesOrganizationService.findAllStatusTrue();
		return ResponseEntity.ok(valueKeyResponseList);
	}

	@PutMapping("/updateSo/{id}")
	public ResponseEntity<Object> updateSo(@PathVariable Long id,
			@Valid @RequestBody SalesOrganizationRequest updateSalesOrganizationRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		SalesOrganizationResponse updateSo = salesOrganizationService.updateSo(id, updateSalesOrganizationRequest);
		return ResponseEntity.ok(updateSo);
	}

	@PatchMapping("/updateSoStatus/{id}")
	public ResponseEntity<Object> updateSoStatus(@PathVariable Long id) throws ResourceNotFoundException {
		SalesOrganizationResponse soResponse = salesOrganizationService.updateSoStatus(id);
		return ResponseEntity.ok(soResponse);
	}

	@PatchMapping("/updateBatchSoStatus")
	public ResponseEntity<Object> updateBatchSoStatus(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		List<SalesOrganizationResponse> soResponses = salesOrganizationService.updateBatchSoStatus(ids);
		return ResponseEntity.ok(soResponses);
	}

	@DeleteMapping("/deleteSo/{id}")
	public ResponseEntity<Object> deleteSo(@PathVariable Long id) throws ResourceNotFoundException {
		salesOrganizationService.deleteSoById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchSo")
	public ResponseEntity<Object> deleteBatchSo(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		salesOrganizationService.deleteBatchSo(ids);
		return ResponseEntity.noContent().build();
	}
}
