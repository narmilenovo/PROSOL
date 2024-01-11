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

import com.example.generalservice.dto.request.BaseUOPRequest;
import com.example.generalservice.dto.response.BaseUOPResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.BaseUOPService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BaseUOPController {
	private final BaseUOPService baseUOPService;

	@PostMapping("/saveUop")
	public ResponseEntity<Object> saveUop(@Valid @RequestBody BaseUOPRequest baseUOPRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveUop").toUriString());
		BaseUOPResponse baseUOPResponse = baseUOPService.saveUop(baseUOPRequest);
		return ResponseEntity.created(uri).body(baseUOPResponse);
	}

	@GetMapping("/getAllUop")
	public ResponseEntity<Object> getAllUop() {
		List<BaseUOPResponse> allUop = baseUOPService.getAllUop();
		return ResponseEntity.ok(allUop);
	}

	@GetMapping("/getUopById/{id}")
	public ResponseEntity<Object> getUopById(@PathVariable Long id) throws ResourceNotFoundException {
		BaseUOPResponse uopResponse = baseUOPService.getUopById(id);
		return ResponseEntity.ok(uopResponse);
	}

	@GetMapping("/getAllUopTrue")
	public ResponseEntity<Object> listUopStatusTrue() {
		List<BaseUOPResponse> uopResponses = baseUOPService.findAllStatusTrue();
		return ResponseEntity.ok(uopResponses);
	}

	@PutMapping("/updateUop/{id}")
	public ResponseEntity<Object> updateUop(@PathVariable Long id,
			@Valid @RequestBody BaseUOPRequest updateBaseUOPRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		BaseUOPResponse uopResponse = baseUOPService.updateUop(id, updateBaseUOPRequest);
		return ResponseEntity.ok(uopResponse);
	}

	@DeleteMapping("/deleteUop/{id}")
	public ResponseEntity<Object> deleteUop(@PathVariable Long id) throws ResourceNotFoundException {
		baseUOPService.deleteUopId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchUop")
	public ResponseEntity<Object> deleteBatchUop(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		baseUOPService.deleteBatchUop(ids);
		return ResponseEntity.ok("Successfully deleted !!!");
	}

}
