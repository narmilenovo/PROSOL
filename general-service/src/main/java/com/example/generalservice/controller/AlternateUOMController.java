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

	@GetMapping("/getAllUom")
	public ResponseEntity<Object> getAllUom() {
		List<AlternateUOMResponse> allUom = alternateUOMService.getAllUom();
		return ResponseEntity.ok(allUom);
	}

	@GetMapping(value = "/getUomById/{id}")
	public ResponseEntity<Object> getUomById(@PathVariable Long id) throws ResourceNotFoundException {
		AlternateUOMResponse uomResponse = alternateUOMService.getUomById(id);
		return ResponseEntity.ok(uomResponse);
	}

	@GetMapping("/getAllUomTrue")
	public ResponseEntity<Object> listUomStatusTrue() {
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

	@DeleteMapping("/deleteUom/{id}")
	public ResponseEntity<Object> deleteUom(@PathVariable Long id) throws ResourceNotFoundException {
		alternateUOMService.deleteUomId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchUom")
	public ResponseEntity<Object> deleteBatchUom(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		alternateUOMService.deleteBatchUom(ids);
		return ResponseEntity.ok("Successfully Deleted");
	}

}
