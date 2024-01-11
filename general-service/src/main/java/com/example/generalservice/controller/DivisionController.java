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

import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.DivisionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DivisionController {

	private final DivisionService divisionService;

	@PostMapping("/saveDivision")
	public ResponseEntity<Object> saveDivision(@Valid @RequestBody DivisionRequest divisionRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDivision").toUriString());
		DivisionResponse divisionResponse = divisionService.saveDivision(divisionRequest);
		return ResponseEntity.created(uri).body(divisionResponse);
	}

	@GetMapping("/getAllDivision")
	public ResponseEntity<Object> getAllDivision() {
		List<DivisionResponse> divisionResponses = divisionService.getAllDivision();
		return ResponseEntity.ok(divisionResponses);
	}

	@GetMapping("/getDivisionById/{id}")
	public ResponseEntity<Object> getDivisionById(@PathVariable Long id) throws ResourceNotFoundException {
		DivisionResponse divisionResponse = divisionService.getDivisionById(id);
		return ResponseEntity.ok(divisionResponse);
	}

	@GetMapping("/getAllDivisionTrue")
	public ResponseEntity<Object> listDivisionStatusTrue() {
		List<DivisionResponse> divisionResponses = divisionService.findAllStatusTrue();
		return ResponseEntity.ok(divisionResponses);
	}

	@PutMapping("/updateDivision/{id}")
	public ResponseEntity<Object> updateDivision(@PathVariable Long id,
			@Valid @RequestBody DivisionRequest updateDivisionRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		DivisionResponse divisionResponse = divisionService.updateDivision(id, updateDivisionRequest);
		return ResponseEntity.ok(divisionResponse);
	}

	@DeleteMapping("/deleteDivision/{id}")
	public ResponseEntity<Object> deleteDivision(@PathVariable Long id) throws ResourceNotFoundException {
		divisionService.deleteDivisionId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchDivsion")
	public ResponseEntity<Object> deleteBatchDivsion(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		divisionService.deleteBatchDivsion(ids);
		return ResponseEntity.ok("Successfully deleted !!!");
	}

}
