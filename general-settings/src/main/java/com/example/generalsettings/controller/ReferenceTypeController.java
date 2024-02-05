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
import com.example.generalsettings.entity.ReferenceType;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.ReferenceTypeRequest;
import com.example.generalsettings.response.ReferenceTypeResponse;
import com.example.generalsettings.service.ReferenceTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReferenceTypeController {
	private final ReferenceTypeService referenceTypeService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveReferenceType")
	public ResponseEntity<Object> saveReferenceType(@Valid @RequestBody ReferenceTypeRequest referenceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveReferenceType").toUriString());
		ReferenceTypeResponse savedReferenceType = referenceTypeService.saveReferenceType(referenceTypeRequest);
		return ResponseEntity.created(uri).body(savedReferenceType);
	}

	@GetMapping("/getReferenceTypeById/{id}")
	public ResponseEntity<Object> getReferenceTypeById(@PathVariable Long id) throws ResourceNotFoundException {
		ReferenceTypeResponse foundReferenceType = referenceTypeService.getReferenceTypeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundReferenceType);
	}

	@GetMapping("/getAllReferenceType")
	public ResponseEntity<Object> getAllReferenceType() {
		List<ReferenceTypeResponse> referenceType = referenceTypeService.getAllReferenceType();
		return ResponseEntity.ok(referenceType);
	}

	@PutMapping("/updateReferenceType/{id}")
	public ResponseEntity<Object> updateReferenceType(@PathVariable Long id,
			@RequestBody ReferenceTypeRequest referenceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		ReferenceTypeResponse updateReferenceType = referenceTypeService.updateReferenceType(id, referenceTypeRequest);
		return ResponseEntity.ok().body(updateReferenceType);
	}

	@PatchMapping("/updateReferenceStatusTypeById/{id}")
	public ResponseEntity<Object> updateReferenceStatusTypeById(@PathVariable Long id)
			throws ResourceNotFoundException {
		ReferenceTypeResponse response = referenceTypeService.updateStatusUsingReferenceTypeId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusReferenceTypeId")
	public ResponseEntity<Object> updateBulkStatusReferenceTypeId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<ReferenceTypeResponse> responseList = referenceTypeService.updateBulkStatusReferenceTypeId(id);
		return ResponseEntity.ok(responseList);
	}

	@PatchMapping("/updateRefrenceDupCheckById/{id}")
	public ResponseEntity<Object> updateRefrenceDupCheckById(@PathVariable Long id) throws ResourceNotFoundException {
		ReferenceTypeResponse response = referenceTypeService.updateRefrenceDupCheckById(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkReferenceDupCheckTypeId")
	public ResponseEntity<Object> updateBulkReferenceDupCheckTypeId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<ReferenceTypeResponse> responseList = referenceTypeService.updateBulkReferenceDupCheckTypeId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteReferenceType/{id}")
	public ResponseEntity<String> deleteReferenceType(@PathVariable Long id) throws ResourceNotFoundException {
		referenceTypeService.deleteReferenceType(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchReferenceType")
	public ResponseEntity<Object> deleteBatchReferenceType(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		referenceTypeService.deleteBatchReferenceType(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/pdfReferenceTypeReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<ReferenceType> referenceTypeReport = referenceTypeService.findAll();
		List<Map<String, Object>> data = referenceTypeService.convertReferenceTypeListToMap(referenceTypeReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "ReferenceTypeReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "ReferenceTypeReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
