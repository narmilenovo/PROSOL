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

import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.IndustrySectorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class IndustrySectorController {

	private final IndustrySectorService industrySectorService;

	@PostMapping("/saveSector")
	public ResponseEntity<Object> saveSector(@Valid @RequestBody IndustrySectorRequest industrySectorRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSector").toUriString());
		IndustrySectorResponse sectorResponse = industrySectorService.saveSector(industrySectorRequest);
		return ResponseEntity.created(uri).body(sectorResponse);
	}

	@GetMapping("/getSectorById/{id}")
	public ResponseEntity<Object> getSectorById(@PathVariable Long id) throws ResourceNotFoundException {
		IndustrySectorResponse sectorResponse = industrySectorService.getSectorById(id);
		return ResponseEntity.status(HttpStatus.OK).body(sectorResponse);
	}

	@GetMapping("/getAllSector")
	public ResponseEntity<Object> getAllSector() {
		List<IndustrySectorResponse> allSector = industrySectorService.getAllSector();
		return ResponseEntity.ok(allSector);
	}

	@GetMapping("/getAllSectorTrue")
	public ResponseEntity<Object> listSectorStatusTrue() {
		List<IndustrySectorResponse> sectorResponses = industrySectorService.findAllStatusTrue();
		return ResponseEntity.ok(sectorResponses);
	}

	@PutMapping("/updateSector/{id}")
	public ResponseEntity<Object> updateSector(@PathVariable Long id,
			@Valid @RequestBody IndustrySectorRequest updateindustrysectorrequest)
			throws ResourceNotFoundException, ResourceFoundException {
		IndustrySectorResponse updateSector = industrySectorService.updateSector(id, updateindustrysectorrequest);
		return ResponseEntity.ok(updateSector);
	}

	@PatchMapping("/updateSectorStatus/{id}")
	public ResponseEntity<Object> updateSectorStatus(@PathVariable Long id) throws ResourceNotFoundException {
		IndustrySectorResponse sectorResponse = industrySectorService.updateSectorStatus(id);
		return ResponseEntity.ok(sectorResponse);
	}

	@PatchMapping("/updateBatchSectorStatus")
	public ResponseEntity<Object> updateBatchSectorStatus(@RequestBody List<Long> ids) {
		List<IndustrySectorResponse> sectorResponses = industrySectorService.updateBatchSectorResponseStatus(ids);
		return ResponseEntity.ok(sectorResponses);
	}

	@DeleteMapping("/deleteSector/{id}")
	public ResponseEntity<Object> deleteSector(@PathVariable Long id) throws ResourceNotFoundException {
		industrySectorService.deleteSectorId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchSector")
	public ResponseEntity<Object> deleteBatchSector(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		industrySectorService.deleteBatchSector(ids);
		return ResponseEntity.noContent().build();
	}
}
