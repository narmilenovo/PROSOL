package com.example.sales_otherservice.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping("/getAllPg")
	public ResponseEntity<Object> getAllPg() {
		List<PurchasingGroupResponse> allPg = purchasingGroupService.getAllPg();
		return ResponseEntity.ok(allPg);
	}

	@GetMapping("/getPgById/{id}")
	public ResponseEntity<Object> getPgById(@PathVariable Long id) throws ResourceNotFoundException {
		PurchasingGroupResponse dpById = purchasingGroupService.getPgById(id);
		return ResponseEntity.ok(dpById);
	}

	@GetMapping("/getAllPgTrue")
	public ResponseEntity<Object> listPgStatusTrue() {
		List<PurchasingGroupResponse> groupResponseList = purchasingGroupService.findAllStatusTrue();
		return ResponseEntity.ok(groupResponseList);
	}

	@PutMapping("/updatePg/{id}")
	public ResponseEntity<Object> updatePg(@PathVariable Long id,
			@Valid @RequestBody PurchasingGroupRequest updatePurchasingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		PurchasingGroupResponse updatePg = purchasingGroupService.updatePg(id, updatePurchasingGroupRequest);
		return ResponseEntity.ok(updatePg);
	}

	@DeleteMapping("/deletePg/{id}")
	public ResponseEntity<Object> deletePg(@PathVariable Long id) throws ResourceNotFoundException {
		purchasingGroupService.deletePgById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchPg")
	public ResponseEntity<Object> deleteBatchPg(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		purchasingGroupService.deleteBatchPg(ids);
		return ResponseEntity.ok("Successfully deleted !!!");
	}
}
