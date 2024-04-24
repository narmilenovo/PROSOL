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

import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.PurchasingGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PurchasingGroupController {
	private final PurchasingGroupService purchasingGroupService;

	@PostMapping("/savePg")
	public ResponseEntity<Object> savePg(@Valid @RequestBody PurchasingGroupRequest purchasingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePg").toUriString());
		PurchasingGroupResponse savePg = purchasingGroupService.savePg(purchasingGroupRequest);
		return ResponseEntity.created(uri).body(savePg);
	}

	@GetMapping("/getPgById/{id}")
	public ResponseEntity<Object> getPgById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		PurchasingGroupResponse dpById = purchasingGroupService.getPgById(id);
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllPg")
	public ResponseEntity<Object> getAllPg() {
		List<PurchasingGroupResponse> allPg = purchasingGroupService.getAllPg();
		return ResponseEntity.ok(allPg);
	}

	@GetMapping("/getAllPgTrue")
	public ResponseEntity<Object> listPgStatusTrue() {
		List<PurchasingGroupResponse> groupResponseList = purchasingGroupService.findAllStatusTrue();
		return ResponseEntity.ok(groupResponseList);
	}

	@PutMapping("/updatePg/{id}")
	public ResponseEntity<Object> updatePg(@PathVariable @NonNull Long id,
			@Valid @RequestBody PurchasingGroupRequest updatePurchasingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		PurchasingGroupResponse updatePg = purchasingGroupService.updatePg(id, updatePurchasingGroupRequest);
		return ResponseEntity.ok(updatePg);
	}

	@PatchMapping("/updatePgStatus/{id}")
	public ResponseEntity<Object> updatePgStatus(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		PurchasingGroupResponse pgResponse = purchasingGroupService.updatePgStatus(id);
		return ResponseEntity.ok(pgResponse);
	}

	@PatchMapping("/updateBatchPgStatus")
	public ResponseEntity<Object> updateBatchPgStatus(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		List<PurchasingGroupResponse> pgResponses = purchasingGroupService.updateBatchPgStatus(ids);
		return ResponseEntity.ok(pgResponses);
	}

	@DeleteMapping("/deletePg/{id}")
	public ResponseEntity<Object> deletePg(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		purchasingGroupService.deletePgById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchPg")
	public ResponseEntity<Object> deleteBatchPg(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		purchasingGroupService.deleteBatchPg(ids);
		return ResponseEntity.noContent().build();
	}
}
