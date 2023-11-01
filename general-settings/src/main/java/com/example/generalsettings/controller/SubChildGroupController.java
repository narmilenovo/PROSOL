package com.example.generalsettings.controller;

import java.net.URI;
import java.util.List;

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

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.SubChildGroupRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;
import com.example.generalsettings.response.SubChildGroupResponse;
import com.example.generalsettings.response.SubMainGroupResponse;
import com.example.generalsettings.service.MainGroupCodesService;
import com.example.generalsettings.service.SubChildGroupService;
import com.example.generalsettings.service.SubMainGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
public class SubChildGroupController {
	 private final SubChildGroupService subChildGroupService;


	    private final MainGroupCodesService mainGroupCodesService;


	    private final SubMainGroupService subMainGroupService;

	    @GetMapping("/getAllSubChildGroup")
	    public ResponseEntity<Object> getAllSubChildGroup() {
	        List<SubChildGroupResponse> subChildGroup = subChildGroupService.getAllSubChildGroup();
	        return ResponseEntity.ok(subChildGroup);
	    }

	    @PutMapping("/updateSubChildGroup/{id}")
	    public ResponseEntity<Object> updateStorageLocation(@PathVariable Long id, @RequestBody SubChildGroupRequest subChildGroupRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        SubChildGroupResponse updateSubChildGroup = subChildGroupService.updateSubChildGroup(id, subChildGroupRequest);
	        return ResponseEntity.ok().body(updateSubChildGroup);
	    }

	    @DeleteMapping("/deleteSubChildGroup/{id}")
	    public ResponseEntity<String> deleteSubChildGroup(@PathVariable Long id) throws ResourceNotFoundException {
	        subChildGroupService.deleteSubChildGroup(id);
	        return ResponseEntity.ok().body("SubChildGroup of '" + id + "' is deleted");
	    }

	    @PostMapping("/saveSubChildGroup")
	    public ResponseEntity<Object> saveSubChildGroup(@Valid @RequestBody SubChildGroupRequest subChildGroupRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSubChildGroup").toUriString());
	        SubChildGroupResponse savedSubChildGroup = subChildGroupService.saveSubChildGroup(subChildGroupRequest);
	        return ResponseEntity.created(uri).body(savedSubChildGroup);
	    }

	    @GetMapping("/getSubChildGroupById/{id}")
	    public ResponseEntity<Object> getSubChildGroupById(@PathVariable Long id) throws ResourceNotFoundException {
	        SubChildGroupResponse foundSubChildGroup = subChildGroupService.getSubChildGroupById(id);
	        return ResponseEntity.ok(foundSubChildGroup);
	    }

	    @PatchMapping("/updateStorageLocationStatusById/{id}")
	    public ResponseEntity<Object> updateSubChildGroupStatusId(@PathVariable Long id) throws ResourceNotFoundException {
	        SubChildGroupResponse response = subChildGroupService.updateStatusUsingSubChildGroupId(id);
	        return ResponseEntity.ok(response);
	    }

	    @PatchMapping("/updateBulkStorageLocationId/{id}")
	    public ResponseEntity<Object> updateBulkStatusSubChildGroupId(@PathVariable List<Long> id) {
	        List<SubChildGroupResponse> responseList = subChildGroupService.updateBulkStatusSubChildGroupId(id);
	        return ResponseEntity.ok(responseList);
	    }

		@GetMapping("/getAllSubMainGroup1")
		public ResponseEntity<Object> getAllSubMainGroup1() {
			List<SubMainGroupResponse> subGroup = subMainGroupService.getAllSubMainGroup();
			return ResponseEntity.ok(subGroup);
		}

		@GetMapping("/getAllMainGroupCodes1")
		public ResponseEntity<Object> getAllMainGroupCodes1() {
			List<MainGroupCodesResponse> mainGroup = mainGroupCodesService.getAllMainGroupCodes();
			return ResponseEntity.ok(mainGroup);
		}
}
