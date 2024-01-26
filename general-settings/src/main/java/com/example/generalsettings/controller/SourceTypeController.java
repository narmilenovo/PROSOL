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
import com.example.generalsettings.entity.SourceType;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.SourceTypeRequest;
import com.example.generalsettings.response.SourceTypeResponse;
import com.example.generalsettings.service.SourceTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SourceTypeController {
	private final SourceTypeService sourceTypeService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveSourceType")
	public ResponseEntity<Object> saveSourceType(@Valid @RequestBody SourceTypeRequest sourceTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSourceType").toUriString());
		SourceTypeResponse savedSourceType = sourceTypeService.saveSourceType(sourceTypeRequest);
		return ResponseEntity.created(uri).body(savedSourceType);
	}

	@GetMapping("/getSourceTypeById/{id}")
	public ResponseEntity<Object> getSourceTypeById(@PathVariable Long id) throws ResourceNotFoundException {
		SourceTypeResponse foundSourceType = sourceTypeService.getSourceTypeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundSourceType);
	}

	@GetMapping("/getAllSourceType")
	public ResponseEntity<Object> getAllSourceType() {
		List<SourceTypeResponse> sourceType = sourceTypeService.getAllSourceType();
		return ResponseEntity.ok(sourceType);
	}

	@PutMapping("/updateSourceType/{id}")
	public ResponseEntity<Object> updateSourceType(@PathVariable Long id,
			@RequestBody SourceTypeRequest sourceTypeRequest) throws ResourceNotFoundException, AlreadyExistsException {
		SourceTypeResponse updateSourceType = sourceTypeService.updateSourceType(id, sourceTypeRequest);
		return ResponseEntity.ok().body(updateSourceType);
	}

	@PatchMapping("/updateSourceTypeById/{id}")
	public ResponseEntity<Object> updateSourceTypeStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		SourceTypeResponse response = sourceTypeService.updateStatusUsingSourceTypeId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusSourceTypeId/{id}")
	public ResponseEntity<Object> updateBulkStatusSourceTypeId(@PathVariable List<Long> id)
			throws ResourceNotFoundException {
		List<SourceTypeResponse> responseList = sourceTypeService.updateBulkStatusSourceTypeId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteSourceType/{id}")
	public ResponseEntity<String> deleteSourceType(@PathVariable Long id) throws ResourceNotFoundException {
		sourceTypeService.deleteSourceType(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchSourceType")
	public ResponseEntity<Object> deleteBatchSourceType(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		sourceTypeService.deleteBatchSourceType(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/pdfSourceTypeReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<SourceType> sourceTypeReport = sourceTypeService.findAll();
		List<Map<String, Object>> data = sourceTypeService.convertSourceTypeListToMap(sourceTypeReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "SourceTypeReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "SourceTypeReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
