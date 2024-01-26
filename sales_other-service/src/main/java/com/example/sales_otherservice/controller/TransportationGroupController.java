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

import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.TransportationGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TransportationGroupController {
	private final TransportationGroupService transportationGroupService;

	@PostMapping("/saveTg")
	public ResponseEntity<Object> saveTg(@Valid @RequestBody TransportationGroupRequest transportationGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveTg").toUriString());
		TransportationGroupResponse saveTg = transportationGroupService.saveTg(transportationGroupRequest);
		return ResponseEntity.created(uri).body(saveTg);
	}

	@GetMapping("/getTgById/{id}")
	public ResponseEntity<Object> getTgById(@PathVariable Long id) throws ResourceNotFoundException {
		TransportationGroupResponse dpById = transportationGroupService.getTgById(id);
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllTg")
	public ResponseEntity<Object> getAllTg() {
		List<TransportationGroupResponse> allTg = transportationGroupService.getAllTg();
		return ResponseEntity.ok(allTg);
	}

	@GetMapping("/getAllTgTrue")
	public ResponseEntity<Object> listTgStatusTrue() {
		List<TransportationGroupResponse> groupResponseList = transportationGroupService.findAllStatusTrue();
		return ResponseEntity.ok(groupResponseList);
	}

	@PutMapping("/updateTg/{id}")
	public ResponseEntity<Object> updateTg(@PathVariable Long id,
			@Valid @RequestBody TransportationGroupRequest updateTransportationGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		TransportationGroupResponse updateTg = transportationGroupService.updateTg(id,
				updateTransportationGroupRequest);
		return ResponseEntity.ok(updateTg);
	}

	@PatchMapping("/updateTgStatus/{id}")
	public ResponseEntity<Object> updateTgStatus(@PathVariable Long id) throws ResourceNotFoundException {
		TransportationGroupResponse tgResponse = transportationGroupService.updateTgStatus(id);
		return ResponseEntity.ok(tgResponse);
	}

	@PatchMapping("/updateBatchTgStatus")
	public ResponseEntity<Object> updateBatchTgStatus(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		List<TransportationGroupResponse> tgResponses = transportationGroupService.updateBatchTgStatus(ids);
		return ResponseEntity.ok(tgResponses);
	}

	@DeleteMapping("/deleteTg/{id}")
	public ResponseEntity<Object> deleteTg(@PathVariable Long id) throws ResourceNotFoundException {
		transportationGroupService.deleteTgById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchTg")
	public ResponseEntity<Object> deleteBatchTg(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		transportationGroupService.deleteBatchTg(ids);
		return ResponseEntity.noContent().build();
	}
}
