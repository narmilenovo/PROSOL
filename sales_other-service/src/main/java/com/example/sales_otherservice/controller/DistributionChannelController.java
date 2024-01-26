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

import com.example.sales_otherservice.dto.request.DistributionChannelRequest;
import com.example.sales_otherservice.dto.response.DistributionChannelResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.DistributionChannelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DistributionChannelController {
	private final DistributionChannelService distributionChannelService;

	@PostMapping("/saveDc")
	public ResponseEntity<Object> saveDc(@Valid @RequestBody DistributionChannelRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDc").toUriString());
		DistributionChannelResponse saveDc = distributionChannelService.saveDc(deliveringPlantRequest);
		return ResponseEntity.created(uri).body(saveDc);
	}

	@GetMapping("/getDcById/{id}")
	public ResponseEntity<Object> getDcById(@PathVariable Long id) throws ResourceNotFoundException {
		DistributionChannelResponse dpById = distributionChannelService.getDcById(id);
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllDc")
	public ResponseEntity<Object> getAllDc() {
		List<DistributionChannelResponse> allDc = distributionChannelService.getAllDc();
		return ResponseEntity.ok(allDc);
	}

	@GetMapping("/getAllDcTrue")
	public ResponseEntity<Object> listDcStatusTrue() {
		List<DistributionChannelResponse> channelResponseList = distributionChannelService.findAllStatusTrue();
		return ResponseEntity.ok(channelResponseList);
	}

	@PutMapping("/updateDc/{id}")
	public ResponseEntity<Object> updateDc(@PathVariable Long id,
			@Valid @RequestBody DistributionChannelRequest updateDistributionChannelRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		DistributionChannelResponse updateDc = distributionChannelService.updateDc(id,
				updateDistributionChannelRequest);
		return ResponseEntity.ok(updateDc);
	}

	@PatchMapping("/updateDcStatus/{id}")
	public ResponseEntity<Object> updateDcStatus(@PathVariable Long id) throws ResourceNotFoundException {
		DistributionChannelResponse dcResponse = distributionChannelService.updateDcStatus(id);
		return ResponseEntity.ok(dcResponse);
	}

	@PatchMapping("/updateBatchDcStatus")
	public ResponseEntity<Object> updateBatchDcStatus(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		List<DistributionChannelResponse> dcResponses = distributionChannelService.updateBatchDcStatus(ids);
		return ResponseEntity.ok(dcResponses);
	}

	@DeleteMapping("/deleteDc/{id}")
	public ResponseEntity<Object> deleteDc(@PathVariable Long id) throws ResourceNotFoundException {
		distributionChannelService.deleteDcId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchDc")
	public ResponseEntity<Object> deleteBatchDc(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		distributionChannelService.deleteBatchDc(ids);
		return ResponseEntity.noContent().build();
	}
}
