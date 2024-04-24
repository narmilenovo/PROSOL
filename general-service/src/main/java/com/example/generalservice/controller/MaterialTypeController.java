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

import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.MaterialTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MaterialTypeController {
	private final MaterialTypeService materialTypeService;

	@PostMapping("/saveMaterial")
	public ResponseEntity<Object> saveMaterial(@Valid @RequestBody MaterialTypeRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveMaterial").toUriString());
		MaterialTypeResponse savedMaterial = materialTypeService.saveMaterial(alternateUOMRequest);
		return ResponseEntity.created(uri).body(savedMaterial);
	}

	@GetMapping("/getMaterialById/{id}")
	public ResponseEntity<Object> getMaterialById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		MaterialTypeResponse materialTypeResponse = materialTypeService.getMaterialById(id);
		return ResponseEntity.status(HttpStatus.OK).body(materialTypeResponse);
	}

	@GetMapping("/getAllMaterial")
	public ResponseEntity<Object> getAllMaterial() {
		List<MaterialTypeResponse> allMaterial = materialTypeService.getAllMaterial();
		return ResponseEntity.ok(allMaterial);
	}

	@GetMapping("/getAllMaterialTrue")
	public ResponseEntity<Object> listMaterialStatusTrue() {
		List<MaterialTypeResponse> materialTypeResponses = materialTypeService.findAllStatusTrue();
		return ResponseEntity.ok(materialTypeResponses);
	}

	@PutMapping("/updateMaterial/{id}")
	public ResponseEntity<Object> updateMaterial(@PathVariable @NonNull Long id,
			@Valid @RequestBody MaterialTypeRequest updateMaterialTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		MaterialTypeResponse updatedMaterial = materialTypeService.updateMaterial(id, updateMaterialTypeRequest);
		return ResponseEntity.ok(updatedMaterial);
	}

	@PatchMapping("/updateMaterialStatus/{id}")
	public ResponseEntity<Object> updateMaterialStatus(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		MaterialTypeResponse updatedMaterial = materialTypeService.updateMaterialStatus(id);
		return ResponseEntity.ok(updatedMaterial);
	}

	@PatchMapping("/updateBatchMaterialStatus")
	public ResponseEntity<Object> updateBatchMaterialStatus(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		List<MaterialTypeResponse> updatedMaterials = materialTypeService.updateBatchMaterialStatus(ids);
		return ResponseEntity.ok(updatedMaterials);
	}

	@DeleteMapping("/deleteMaterial/{id}")
	public ResponseEntity<Object> deleteMaterial(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		materialTypeService.deleteMaterialId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchMaterial")
	public ResponseEntity<Object> deleteBatchMaterial(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		materialTypeService.deleteBatchMaterial(ids);
		return ResponseEntity.noContent().build();
	}
}
