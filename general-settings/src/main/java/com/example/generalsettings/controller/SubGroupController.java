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
import com.example.generalsettings.entity.SubGroupCodes;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.SubGroupCodesRequest;
import com.example.generalsettings.response.SubGroupCodesResponse;
import com.example.generalsettings.service.MainGroupCodesService;
import com.example.generalsettings.service.SubGroupService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SubGroupController {
	private final SubGroupService subGroupService;

	private final GeneratePdfReport generatePdfReport;
	private final MainGroupCodesService mainGroupCodesService;

	@PostMapping("/saveSubGroupCodes")
	public ResponseEntity<Object> saveSubGroupCodes(@Valid @RequestBody SubGroupCodesRequest subGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSubMainGroup").toUriString());
		SubGroupCodesResponse savedSubMainGroup = subGroupService.saveSubMainGroup(subGroupCodesRequest);
		return ResponseEntity.created(uri).body(savedSubMainGroup);
	}

	@GetMapping("/getSubGroupCodesById/{id}")
	public ResponseEntity<Object> getSubGroupCodesById(@PathVariable Long id) throws ResourceNotFoundException {
		SubGroupCodesResponse foundSubMainGroup = subGroupService.getSubMainGroupById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundSubMainGroup);
	}

	@GetMapping("/getAllSubGroupCodes")
	public ResponseEntity<Object> getAllSubGroupCodes() {
		List<SubGroupCodesResponse> subMainGroups = subGroupService.getAllSubMainGroup();
		return ResponseEntity.ok(subMainGroups);
	}

	@GetMapping("/getAllSubGroupCodesByMainGroupId/{id}")
	public ResponseEntity<Object> getAllSubGroupCodesByMainGroupId(@PathVariable Long id) {
		List<SubGroupCodesResponse> subGroups = subGroupService.getAllSubGroupCodesByMainGroupId(id);
		return ResponseEntity.ok(subGroups);
	}

	@PutMapping("/updateSubGroupCodes/{id}")
	public ResponseEntity<Object> updateSubGroupCodes(@PathVariable Long id,
			@RequestBody SubGroupCodesRequest subGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		SubGroupCodesResponse updatesubMainGroup = subGroupService.updateSubMainGroup(id, subGroupCodesRequest);
		return ResponseEntity.ok().body(updatesubMainGroup);
	}

	@PatchMapping("/updateSubGroupCodesStatusById/{id}")
	public ResponseEntity<Object> updateSubGroupCodesStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		SubGroupCodesResponse response = subGroupService.updateStatusUsingSubMainGroupId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusSubGroupCodestId")
	public ResponseEntity<Object> updateBulkStatusSubGroupCodesId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<SubGroupCodesResponse> responseList = subGroupService.updateBulkStatusSubMainGroupId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteSubGroupCodes/{id}")
	public ResponseEntity<String> deleteSubGroupCodes(@PathVariable Long id) throws ResourceNotFoundException {
		subGroupService.deleteSubMainGroup(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchSubGroupCodes")
	public ResponseEntity<Object> deleteBatchSubGroupCodes(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		subGroupService.deleteBatchSubGroupCodes(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/pdfSubGroupCodesReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<SubGroupCodes> subGroupCodesReport = subGroupService.findAll();
		List<Map<String, Object>> data = subGroupService.convertSubMainGroupCodesListToMap(subGroupCodesReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "SubMainGroupReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "SubMainGroupReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
