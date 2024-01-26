
package com.example.attributemaster.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.attributemaster.exception.AlreadyExistsException;
import com.example.attributemaster.exception.ResourceNotFoundException;
import com.example.attributemaster.request.AttributeMasterRequest;
import com.example.attributemaster.response.AttributeMasterResponse;
import com.example.attributemaster.service.AttributeMasterService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AttributeMasterController {
	private final AttributeMasterService attributeMasterService;

	@PostMapping("/saveAttributeMaster")
	public ResponseEntity<Object> saveAttributeMaster(@Valid @RequestBody AttributeMasterRequest attributeMasterRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveAttributeMaster").toUriString());
		AttributeMasterResponse savedAttributeMaster = attributeMasterService
				.saveAttributeMaster(attributeMasterRequest);
		return ResponseEntity.created(uri).body(savedAttributeMaster);
	}

	@GetMapping("/getAttributeMasterById/{id}")
	public ResponseEntity<Object> getAttributeMasterById(@PathVariable Long id,
			@RequestParam(required = false) Boolean showFull) throws ResourceNotFoundException {
		Object foundAttributeMaster;

		if (Boolean.TRUE.equals(showFull)) {
			foundAttributeMaster = attributeMasterService.getAttributeMasterUomById(id);
		} else {
			foundAttributeMaster = attributeMasterService.getAttributeMasterById(id);
		}

		return ResponseEntity.status(HttpStatus.OK).body(foundAttributeMaster);
	}

	@GetMapping("/getAllAttributeFromMaster")
	public ResponseEntity<Object> getAllAttributeFromMaster(@RequestParam(required = false) Boolean showFull)
			throws ResourceNotFoundException {
		List<?> response;
		if (Boolean.TRUE.equals(showFull)) {
			response = attributeMasterService.getAllAttributeMasterUom();
		} else {
			response = attributeMasterService.getAllAttributeMaster();
		}
		return ResponseEntity.ok(response);
	}

	@PutMapping("/updateAttributeMaster/{id}")
	public ResponseEntity<Object> updateAttributeMaster(@PathVariable Long id,
			@RequestBody AttributeMasterRequest attributeMasterRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		AttributeMasterResponse updateAttributeMaster = attributeMasterService.updateAttributeMaster(id,
				attributeMasterRequest);
		return ResponseEntity.ok().body(updateAttributeMaster);
	}

	@DeleteMapping("/deleteAttributeMaster/{id}")
	public ResponseEntity<Object> deleteAttributeMaster(@PathVariable Long id) throws ResourceNotFoundException {
		attributeMasterService.deleteAttributeMaster(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@DeleteMapping("/deleteBatchMaster")
	public ResponseEntity<Object> deleteBatchMaster(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		attributeMasterService.deleteBatchMaster(ids);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
