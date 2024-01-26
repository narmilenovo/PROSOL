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

import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.LoadingGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LoadingGroupController {
	private final LoadingGroupService loadingGroupService;

	@PostMapping("/saveLg")
	public ResponseEntity<Object> saveLg(@Valid @RequestBody LoadingGroupRequest loadingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveLg").toUriString());
		LoadingGroupResponse saveLg = loadingGroupService.saveLg(loadingGroupRequest);
		return ResponseEntity.created(uri).body(saveLg);
	}

	@GetMapping("/getLgById/{id}")
	public ResponseEntity<Object> getLgById(@PathVariable Long id) throws ResourceNotFoundException {
		LoadingGroupResponse dpById = loadingGroupService.getLgById(id);
		return ResponseEntity.status(HttpStatus.FOUND).body(dpById);
	}

	@GetMapping("/getAllLg")
	public ResponseEntity<Object> getAllLg() {
		List<LoadingGroupResponse> allLg = loadingGroupService.getAllLg();
		return ResponseEntity.ok(allLg);
	}

	@GetMapping("/getAllLgTrue")
	public ResponseEntity<Object> listLgStatusTrue() {
		List<LoadingGroupResponse> groupResponses = loadingGroupService.findAllStatusTrue();
		return ResponseEntity.ok(groupResponses);
	}

	@PutMapping("/updateLg/{id}")
	public ResponseEntity<Object> updateLg(@PathVariable Long id,
			@Valid @RequestBody LoadingGroupRequest updateLoadingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		LoadingGroupResponse updateLg = loadingGroupService.updateLg(id, updateLoadingGroupRequest);
		return ResponseEntity.ok(updateLg);
	}

	@PatchMapping("/updateLgStatus/{id}")
	public ResponseEntity<Object> updateLgStatus(@PathVariable Long id) throws ResourceNotFoundException {
		LoadingGroupResponse lgResponse = loadingGroupService.updateLgStatus(id);
		return ResponseEntity.ok(lgResponse);
	}

	@PatchMapping("/updateBatchLgStatus")
	public ResponseEntity<Object> updateBatchLgStatus(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		List<LoadingGroupResponse> lgResponses = loadingGroupService.updateBatchLgStatus(ids);
		return ResponseEntity.ok(lgResponses);
	}

	@DeleteMapping("/deleteLg/{id}")
	public ResponseEntity<Object> deleteLg(@PathVariable Long id) throws ResourceNotFoundException {
		loadingGroupService.deleteLgById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchLg")
	public ResponseEntity<Object> deleteBatchLg(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		loadingGroupService.deleteBatchLg(ids);
		return ResponseEntity.noContent().build();
	}
}
