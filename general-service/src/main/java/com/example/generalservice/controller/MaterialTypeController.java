package com.example.generalservice.controller;

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

	@GetMapping("/getAllMaterial")
	public ResponseEntity<Object> getAllMaterial() {
		List<MaterialTypeResponse> allMaterial = materialTypeService.getAllMaterial();
		return ResponseEntity.ok(allMaterial);
	}

	@GetMapping("/getMaterialById/{id}")
	public ResponseEntity<Object> getMaterialById(@PathVariable Long id) throws ResourceNotFoundException {
		MaterialTypeResponse materialTypeResponse = materialTypeService.getMaterialById(id);
		return ResponseEntity.ok(materialTypeResponse);
	}

	@GetMapping("/getAllMaterialTrue")
	public ResponseEntity<Object> listMaterialStatusTrue() {
		List<MaterialTypeResponse> materialTypeResponses = materialTypeService.findAllStatusTrue();
		return ResponseEntity.ok(materialTypeResponses);
	}

	@PutMapping("/updateMaterial/{id}")
	public ResponseEntity<Object> updateMaterial(@PathVariable Long id,
			@Valid @RequestBody MaterialTypeRequest updateMaterialTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		MaterialTypeResponse updatedMaterial = materialTypeService.updateMaterial(id, updateMaterialTypeRequest);
		return ResponseEntity.ok(updatedMaterial);
	}

	@DeleteMapping("/deleteMaterial/{id}")
	public ResponseEntity<Object> deleteMaterial(@PathVariable Long id) throws ResourceNotFoundException {
		materialTypeService.deleteMaterialId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchMaterial")
	public ResponseEntity<Object> deleteBatchMaterial(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		materialTypeService.deleteBatchMaterial(ids);
		return ResponseEntity.ok("Successfully deleted !!!");
	}
}
