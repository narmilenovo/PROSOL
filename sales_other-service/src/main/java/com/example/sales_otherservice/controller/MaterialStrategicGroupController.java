package com.example.sales_otherservice.controller;

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

import com.example.sales_otherservice.dto.request.MaterialStrategicGroupRequest;
import com.example.sales_otherservice.dto.response.MaterialStrategicGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.MaterialStrategicGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MaterialStrategicGroupController {
	private final MaterialStrategicGroupService materialStrategicGroupService;

	@PostMapping("/saveMsg")
	public ResponseEntity<Object> saveMsg(
			@Valid @RequestBody MaterialStrategicGroupRequest materialStrategicGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveMsg").toUriString());
		MaterialStrategicGroupResponse saveMsg = materialStrategicGroupService.saveMsg(materialStrategicGroupRequest);
		return ResponseEntity.created(uri).body(saveMsg);
	}

	@GetMapping("/getAllMsg")
	public ResponseEntity<Object> getAllMsg() {
		List<MaterialStrategicGroupResponse> allMsg = materialStrategicGroupService.getAllMsg();
		return ResponseEntity.ok(allMsg);
	}

	@GetMapping("/getMsgById/{id}")
	public ResponseEntity<Object> getMsgById(@PathVariable Long id) throws ResourceNotFoundException {
		MaterialStrategicGroupResponse dpById = materialStrategicGroupService.getMsgById(id);
		return ResponseEntity.ok(dpById);
	}

	@GetMapping("/getAllMsgTrue")
	public ResponseEntity<Object> listMsgStatusTrue() {
		List<MaterialStrategicGroupResponse> groupResponses = materialStrategicGroupService.findAllStatusTrue();
		return ResponseEntity.ok(groupResponses);
	}

	@PutMapping("/updateMsg/{id}")
	public ResponseEntity<Object> updateMsg(@PathVariable Long id,
			@Valid @RequestBody MaterialStrategicGroupRequest updateMaterialStrategicGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		MaterialStrategicGroupResponse updateMsg = materialStrategicGroupService.updateMsg(id,
				updateMaterialStrategicGroupRequest);
		return ResponseEntity.ok(updateMsg);
	}

	@DeleteMapping("/deleteMsg/{id}")
	public ResponseEntity<Object> deleteMsg(@PathVariable Long id) throws ResourceNotFoundException {
		materialStrategicGroupService.deleteMsgById(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchMsg")
	public ResponseEntity<Object> deleteBatchMsg(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		materialStrategicGroupService.deleteBatchMsg(ids);
		return ResponseEntity.ok("Successfully deleted !!!");
	}
}
