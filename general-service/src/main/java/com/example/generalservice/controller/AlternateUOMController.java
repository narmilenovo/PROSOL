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

import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.AlternateUOMService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AlternateUOMController {

	private final AlternateUOMService alternateUOMService;

	@PostMapping("/saveUom")
	public ResponseEntity<Object> saveUom(@Valid @RequestBody AlternateUOMRequest alternateUOMRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveUom").toUriString());
		AlternateUOMResponse alternateUOMResponse = alternateUOMService.saveUom(alternateUOMRequest);
		return ResponseEntity.created(uri).body(alternateUOMResponse);
	}

	@GetMapping(value = "/getUomById/{id}")
	public ResponseEntity<Object> getUomById(@PathVariable Long id) throws ResourceNotFoundException {
		AlternateUOMResponse uomResponse = alternateUOMService.getUomById(id);
		return ResponseEntity.status(HttpStatus.OK).body(uomResponse);
	}

	@GetMapping("/getAllUom")
	public ResponseEntity<Object> getAllUom() throws ResourceNotFoundException {
		List<AlternateUOMResponse> allUom = alternateUOMService.getAllUom();
		return ResponseEntity.ok(allUom);
	}

	@GetMapping("/getAllUomTrue")
	public ResponseEntity<Object> listUomStatusTrue() throws ResourceNotFoundException {
		List<AlternateUOMResponse> uomResponses = alternateUOMService.findAllStatusTrue();
		return ResponseEntity.ok(uomResponses);
	}

	@PutMapping("/updateUom/{id}")
	public ResponseEntity<Object> updateUom(@PathVariable Long id,
			@Valid @RequestBody AlternateUOMRequest updateAlternateUOMRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		AlternateUOMResponse uomResponse = alternateUOMService.updateUom(id, updateAlternateUOMRequest);
		return ResponseEntity.ok(uomResponse);
	}

	@PatchMapping("/updateUomStatus/{id}")
	public ResponseEntity<Object> updateUomStatus(@PathVariable Long id) throws ResourceNotFoundException {
		AlternateUOMResponse uomResponse = alternateUOMService.updateUomStatus(id);
		return ResponseEntity.ok(uomResponse);
	}

	@PatchMapping("/updateBatchUomStatus")
	public ResponseEntity<Object> updateBatchUomStatus(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		List<AlternateUOMResponse> uomResponses = alternateUOMService.updateBatchUomStatus(ids);
		return ResponseEntity.ok(uomResponses);
	}

	@DeleteMapping("/deleteUom/{id}")
	public ResponseEntity<Object> deleteUom(@PathVariable Long id) throws ResourceNotFoundException {
		alternateUOMService.deleteUomId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchUom")
	public ResponseEntity<Object> deleteBatchUom(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		alternateUOMService.deleteBatchUom(ids);
		return ResponseEntity.noContent().build();
	}

}
