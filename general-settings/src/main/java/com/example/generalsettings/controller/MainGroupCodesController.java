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
import com.example.generalsettings.entity.MainGroupCodes;
import com.example.generalsettings.exception.AlreadyExistsException;
import com.example.generalsettings.exception.ResourceNotFoundException;
import com.example.generalsettings.request.MainGroupCodesRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;
import com.example.generalsettings.service.MainGroupCodesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MainGroupCodesController {
	private final MainGroupCodesService mainGroupCodesService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveMainGroupCodes")
	public ResponseEntity<Object> saveMainGroupCodes(@Valid @RequestBody MainGroupCodesRequest mainGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveMainGroupCodes").toUriString());
		MainGroupCodesResponse savedMainGroupCodes = mainGroupCodesService.saveMainGroupCodes(mainGroupCodesRequest);
		return ResponseEntity.created(uri).body(savedMainGroupCodes);
	}

	@GetMapping("/getMainGroupCodesById/{id}")
	public ResponseEntity<Object> getMainGroupCodesById(@PathVariable Long id) throws ResourceNotFoundException {
		MainGroupCodesResponse foundMainGroupCodes = mainGroupCodesService.getMainGroupCodesById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundMainGroupCodes);
	}

	@GetMapping("/getAllMainGroupCodes")
	public ResponseEntity<Object> getAllMainGroupCodes() {
		List<MainGroupCodesResponse> mainGroupCodes = mainGroupCodesService.getAllMainGroupCodes();
		return ResponseEntity.ok(mainGroupCodes);
	}

	@PutMapping("/updateMainGroupCodes/{id}")
	public ResponseEntity<Object> updateMainGroupCodes(@PathVariable Long id,
			@RequestBody MainGroupCodesRequest mainGroupCodesRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		MainGroupCodesResponse updateMainGroupCodes = mainGroupCodesService.updateMainGroupCodes(id,
				mainGroupCodesRequest);
		return ResponseEntity.ok().body(updateMainGroupCodes);
	}

	@PatchMapping("/updateMainGroupCodesById/{id}")
	public ResponseEntity<Object> updateMainGroupCodesStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		MainGroupCodesResponse response = mainGroupCodesService.updateStatusUsingMainGroupCodesId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusMainGroupCodesId/{id}")
	public ResponseEntity<Object> updateBulkStatusMainGroupCodesId(@PathVariable List<Long> id)
			throws ResourceNotFoundException {
		List<MainGroupCodesResponse> responseList = mainGroupCodesService.updateBulkStatusMainGroupCodesId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteMainGroupCodes/{id}")
	public ResponseEntity<String> deleteMainGroupCodes(@PathVariable Long id) throws ResourceNotFoundException {
		mainGroupCodesService.deleteMainGroupCodes(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchMainGroupCodes")
	public ResponseEntity<Object> deleteBatchMainGroupCodes(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		mainGroupCodesService.deleteBatchMainGroupCodes(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping(value = "/pdfMainGroupReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<MainGroupCodes> mainGroupCodeReport = mainGroupCodesService.findAll();
		List<Map<String, Object>> data = mainGroupCodesService.convertMainGroupCodesListToMap(mainGroupCodeReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "MainGroupCodesReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "MainGroupCodesReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
