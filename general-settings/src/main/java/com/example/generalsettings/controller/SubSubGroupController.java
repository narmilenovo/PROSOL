package com.example.generalsettings.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.example.generalsettings.config.GeneratePdfReport;
import com.example.generalsettings.entity.SubSubGroup;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.SubSubGroupRequest;
import com.example.generalsettings.response.SubSubGroupResponse;
import com.example.generalsettings.service.MainGroupCodesService;
import com.example.generalsettings.service.SubGroupService;
import com.example.generalsettings.service.SubSubGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SubSubGroupController {
	private final SubSubGroupService subSubGroupService;

	private final GeneratePdfReport generatePdfReport;
	private final MainGroupCodesService mainGroupCodesService;

	private final SubGroupService subGroupService;

	@PostMapping("/saveSubSubGroup")
	public ResponseEntity<Object> saveSubSubGroup(@Valid @RequestBody SubSubGroupRequest subSubGroupRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSubChildGroup").toUriString());
		SubSubGroupResponse savedSubChildGroup = subSubGroupService.saveSubChildGroup(subSubGroupRequest);
		return ResponseEntity.created(uri).body(savedSubChildGroup);
	}

	@GetMapping("/getSubSubGroupById/{id}")
	public ResponseEntity<Object> getSubSubGroupById(@PathVariable Long id) throws ResourceNotFoundException {
		SubSubGroupResponse foundSubChildGroup = subSubGroupService.getSubChildGroupById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundSubChildGroup);
	}

	@GetMapping("/getAllSubSubGroup")
	public ResponseEntity<Object> getAllSubSubGroup() {
		List<SubSubGroupResponse> subChildGroup = subSubGroupService.getAllSubChildGroup();
		return ResponseEntity.ok(subChildGroup);
	}

	@PutMapping("/updateSubSubGroup/{id}")
	public ResponseEntity<Object> updateStorageLocation(@PathVariable Long id,
			@RequestBody SubSubGroupRequest subSubGroupRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		SubSubGroupResponse updateSubChildGroup = subSubGroupService.updateSubChildGroup(id, subSubGroupRequest);
		return ResponseEntity.ok().body(updateSubChildGroup);
	}

	@PatchMapping("/updateSubSubGroupStatusById/{id}")
	public ResponseEntity<Object> updateSubSubGroupStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		SubSubGroupResponse response = subSubGroupService.updateStatusUsingSubChildGroupId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusSubSubGroupId")
	public ResponseEntity<Object> updateBulkStatusSubSubGroupId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<SubSubGroupResponse> responseList = subSubGroupService.updateBulkStatusSubChildGroupId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteSubSubGroup/{id}")
	public ResponseEntity<String> deleteSubSubGroup(@PathVariable Long id) throws ResourceNotFoundException {
		subSubGroupService.deleteSubChildGroup(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchSubSubGroup")
	public ResponseEntity<Object> deleteBatchSubSubGroup(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		subSubGroupService.deleteBatchSubSubGroup(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/pdfSubSubGroupReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<SubSubGroup> subChildGroupCodesReport = subSubGroupService.findAll();
		List<Map<String, Object>> data = subSubGroupService
				.convertSubChildGroupCodesListToMap(subChildGroupCodesReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "SubChildGroupCodesReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "SubChildGroupCodesReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
