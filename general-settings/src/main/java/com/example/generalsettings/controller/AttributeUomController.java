package com.example.generalsettings.controller;

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

import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.AttributeUomRequest;
import com.example.generalsettings.response.AttributeUomResponse;
import com.example.generalsettings.service.AttributeUomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AttributeUomController {
	private final AttributeUomService attributeUomService;

	@PostMapping("/saveAttributeUom")
	public ResponseEntity<Object> saveAttributeUom(@Valid @RequestBody AttributeUomRequest attributeUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveAttributeUom").toUriString());
		AttributeUomResponse savedAttributeUom = attributeUomService.saveAttributeUom(attributeUomRequest);
		return ResponseEntity.created(uri).body(savedAttributeUom);
	}

	@GetMapping("/getAttributeUomById/{id}")
	public ResponseEntity<Object> getAttributeUomById(@PathVariable Long id) throws ResourceNotFoundException {
		AttributeUomResponse foundAttributeUom = attributeUomService.getAttributeUomById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundAttributeUom);
	}

	@GetMapping("/getAllAttributeUom")
	public ResponseEntity<Object> getAllAttributeUom() {
		List<AttributeUomResponse> attributeUom = attributeUomService.getAllAttributeUom();
		return ResponseEntity.ok(attributeUom);
	}

	@PutMapping("/updateAttributeUom/{id}")
	public ResponseEntity<Object> updateAttributeUom(@PathVariable Long id,
			@RequestBody AttributeUomRequest attributeUomRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		AttributeUomResponse updateAttributeUom = attributeUomService.updateAttributeUom(id, attributeUomRequest);
		return ResponseEntity.ok().body(updateAttributeUom);
	}

	@PatchMapping("/updateAttributeUomById/{id}")
	public ResponseEntity<Object> updateAttributeUomStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		AttributeUomResponse response = attributeUomService.updateStatusUsingAttributeUomId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusAttributeUomId")
	public ResponseEntity<Object> updateBulkStatusAttributeUomId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<AttributeUomResponse> responseList = attributeUomService.updateBulkStatusAttributeUomId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteAttributeUom/{id}")
	public ResponseEntity<Object> deleteAttributeUom(@PathVariable Long id) throws ResourceNotFoundException {
		attributeUomService.deleteAttributeUom(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchAttributeUom")
	public ResponseEntity<Object> deleteBatchAttributeUom(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		attributeUomService.deleteBatchAttributeUom(ids);
		return ResponseEntity.noContent().build();
	}

}
