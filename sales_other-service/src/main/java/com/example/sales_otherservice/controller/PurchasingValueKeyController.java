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

import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.PurchasingValueKeyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PurchasingValueKeyController {
	private final PurchasingValueKeyService purchasingValueKeyService;

	@PostMapping("/savePvk")
	public ResponseEntity<Object> savePvk(@Valid @RequestBody PurchasingValueKeyRequest purchasingValueKeyRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePvk").toUriString());
		PurchasingValueKeyResponse savePvk = purchasingValueKeyService.savePvk(purchasingValueKeyRequest);
		return ResponseEntity.created(uri).body(savePvk);
	}

	@GetMapping("/getPvkById/{id}")
	public ResponseEntity<Object> getPvkById(@PathVariable Long id) throws ResourceNotFoundException {
		PurchasingValueKeyResponse dpById = purchasingValueKeyService.getPvkById(id);
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllPvk")
	public ResponseEntity<Object> getAllPvk() {
		List<PurchasingValueKeyResponse> allPvk = purchasingValueKeyService.getAllPvk();
		return ResponseEntity.ok(allPvk);
	}

	@GetMapping("/getAllPvkTrue")
	public ResponseEntity<Object> listPvkStatusTrue() {
		List<PurchasingValueKeyResponse> valueKeyResponseList = purchasingValueKeyService.findAllStatusTrue();
		return ResponseEntity.ok(valueKeyResponseList);
	}

	@PutMapping("/updatePvk/{id}")
	public ResponseEntity<Object> updatePvk(@PathVariable Long id,
			@Valid @RequestBody PurchasingValueKeyRequest updatePurchasingValueKeyRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		PurchasingValueKeyResponse updatePvk = purchasingValueKeyService.updatePvk(id, updatePurchasingValueKeyRequest);
		return ResponseEntity.ok(updatePvk);
	}

	@PatchMapping("/updatePvkStatus/{id}")
	public ResponseEntity<Object> updatePvkStatus(@PathVariable Long id) throws ResourceNotFoundException {
		PurchasingValueKeyResponse pvkResponse = purchasingValueKeyService.updatePvkStatus(id);
		return ResponseEntity.ok(pvkResponse);
	}

	@PatchMapping("/updateBatchPvkStatus")
	public ResponseEntity<Object> updateBatchPvkStatus(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		List<PurchasingValueKeyResponse> pvkResponses = purchasingValueKeyService.updateBatchPvkStatus(ids);
		return ResponseEntity.ok(pvkResponses);
	}

	@DeleteMapping("/deletePvk/{id}")
	public ResponseEntity<Object> deletePvk(@PathVariable Long id) throws ResourceNotFoundException {
		purchasingValueKeyService.deletePvkById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchPvk")
	public ResponseEntity<Object> deleteBatchPvk(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		purchasingValueKeyService.deleteBatchPvk(ids);
		return ResponseEntity.noContent().build();
	}
}
