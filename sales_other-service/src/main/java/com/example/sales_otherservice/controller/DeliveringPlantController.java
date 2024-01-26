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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.DeliveringPlantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DeliveringPlantController {
	private final DeliveringPlantService deliveringPlantService;

	@PostMapping("/saveDp")
	public ResponseEntity<Object> saveDp(@Valid @RequestBody DeliveringPlantRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDp").toUriString());
		DeliveringPlantResponse saveDp = deliveringPlantService.saveDp(deliveringPlantRequest);
		return ResponseEntity.created(uri).body(saveDp);
	}

	@GetMapping("/getDpById/{id}")
	public ResponseEntity<Object> getDpById(@PathVariable Long id, @RequestParam Boolean plant)
			throws ResourceNotFoundException {
		Object dpById;
		if (Boolean.TRUE.equals(plant)) {
			dpById = deliveringPlantService.getDpPlantById(id);
		} else {
			dpById = deliveringPlantService.getDpById(id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllDp")
	public ResponseEntity<Object> getAllDp(@RequestParam Boolean plant) {
		List<?> allDp;
		if (Boolean.TRUE.equals(plant)) {
			allDp = deliveringPlantService.getAllDpPlant();
		} else {
			allDp = deliveringPlantService.getAllDp();
		}
		return ResponseEntity.ok(allDp);
	}

	@GetMapping("/getAllDpTrue")
	public ResponseEntity<Object> listDpStatusTrue() {
		List<DeliveringPlantResponse> plantResponseList = deliveringPlantService.findAllStatusTrue();
		return ResponseEntity.ok(plantResponseList);
	}

	@PutMapping("/updateDp/{id}")
	public ResponseEntity<Object> updateDp(@PathVariable Long id,
			@Valid @RequestBody DeliveringPlantRequest updateDeliveringPlantRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		DeliveringPlantResponse updateDp = deliveringPlantService.updateDp(id, updateDeliveringPlantRequest);
		return ResponseEntity.ok(updateDp);
	}

	@PatchMapping("/updateDpStatus/{id}")
	public ResponseEntity<Object> updateDpStatus(@PathVariable Long id) throws ResourceNotFoundException {
		DeliveringPlantResponse dpResponse = deliveringPlantService.updateDpStatus(id);
		return ResponseEntity.ok(dpResponse);
	}

	@PatchMapping("/updateBatchDpStatus")
	public ResponseEntity<Object> updateBatchDpStatus(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		List<DeliveringPlantResponse> dpResponses = deliveringPlantService.updateBatchDpStatus(ids);
		return ResponseEntity.ok(dpResponses);
	}

	@DeleteMapping("/deleteDp/{id}")
	public ResponseEntity<Object> deleteDp(@PathVariable Long id) throws ResourceNotFoundException {
		deliveringPlantService.deleteDpId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchDp")
	public ResponseEntity<Object> deleteBatchDp(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		deliveringPlantService.deleteBatchDp(ids);
		return ResponseEntity.noContent().build();
	}
}
