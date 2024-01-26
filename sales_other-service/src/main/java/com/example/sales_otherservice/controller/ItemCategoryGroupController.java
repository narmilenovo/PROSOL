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

import com.example.sales_otherservice.dto.request.ItemCategoryGroupRequest;
import com.example.sales_otherservice.dto.response.ItemCategoryGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.ItemCategoryGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ItemCategoryGroupController {
	private final ItemCategoryGroupService itemCategoryGroupService;

	@PostMapping("/saveIcg")
	public ResponseEntity<Object> saveIcg(@Valid @RequestBody ItemCategoryGroupRequest itemCategoryGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveIcg").toUriString());
		ItemCategoryGroupResponse saveIcg = itemCategoryGroupService.saveIcg(itemCategoryGroupRequest);
		return ResponseEntity.created(uri).body(saveIcg);
	}

	@GetMapping("/getIcgById/{id}")
	public ResponseEntity<Object> getIcgById(@PathVariable Long id) throws ResourceNotFoundException {
		ItemCategoryGroupResponse dpById = itemCategoryGroupService.getIcgById(id);
		return ResponseEntity.status(HttpStatus.OK).body(dpById);
	}

	@GetMapping("/getAllIcg")
	public ResponseEntity<Object> getAllIcg() {
		List<ItemCategoryGroupResponse> allIcg = itemCategoryGroupService.getAllIcg();
		return ResponseEntity.ok(allIcg);
	}

	@GetMapping("/getAllIcgTrue")
	public ResponseEntity<Object> listIcgStatusTrue() {
		List<ItemCategoryGroupResponse> groupResponses = itemCategoryGroupService.findAllStatusTrue();
		return ResponseEntity.ok(groupResponses);
	}

	@PutMapping("/updateIcg/{id}")
	public ResponseEntity<Object> updateIcg(@PathVariable Long id,
			@Valid @RequestBody ItemCategoryGroupRequest updateItemCategoryGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		ItemCategoryGroupResponse updateIcg = itemCategoryGroupService.updateIcg(id, updateItemCategoryGroupRequest);
		return ResponseEntity.ok(updateIcg);
	}

	@PatchMapping("/updateIcgStatus/{id}")
	public ResponseEntity<Object> updateIcgStatus(@PathVariable Long id) throws ResourceNotFoundException {
		ItemCategoryGroupResponse icgResponse = itemCategoryGroupService.updateIcgStatus(id);
		return ResponseEntity.ok(icgResponse);
	}

	@PatchMapping("/updateBatchIcgStatus")
	public ResponseEntity<Object> updateBatchIcgStatus(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		List<ItemCategoryGroupResponse> icgResponses = itemCategoryGroupService.updateBatchIcgStatus(ids);
		return ResponseEntity.ok(icgResponses);
	}

	@DeleteMapping("/deleteIcg/{id}")
	public ResponseEntity<Object> deleteIcg(@PathVariable Long id) throws ResourceNotFoundException {
		itemCategoryGroupService.deleteIcgById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchIcg")
	public ResponseEntity<Object> deleteBatchIcg(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		itemCategoryGroupService.deleteBatchIcg(ids);
		return ResponseEntity.noContent().build();
	}

}
