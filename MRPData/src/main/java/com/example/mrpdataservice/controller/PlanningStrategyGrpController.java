package com.example.mrpdataservice.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.mrpdataservice.configuration.GeneratePdfReport;
import com.example.mrpdataservice.entity.PlanningStrategyGrp;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.PlanningStrgyGrpRequest;
import com.example.mrpdataservice.response.PlanningStrgyGrpResponse;
import com.example.mrpdataservice.service.PlanningStrgyGrpService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlanningStrategyGrpController {
	private final GeneratePdfReport generatePdfReport;
	private final PlanningStrgyGrpService planningStrgyGrpService;

	@PostMapping("/savePlanningStrgyGrp")
	public ResponseEntity<Object> savePlanningStrgyGrp(
			@Valid @RequestBody PlanningStrgyGrpRequest planningStrgyGrpRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePlanningStrgyGrp").toUriString());
		PlanningStrgyGrpResponse savedPlanningStrgyGrp = planningStrgyGrpService
				.savePlanningStrgyGrp(planningStrgyGrpRequest);
		return ResponseEntity.created(uri).body(savedPlanningStrgyGrp);
	}

	@GetMapping("/getPlanningStrgyGrpById/{id}")
	public ResponseEntity<Object> getPlanningStrgyGrpById(@PathVariable Long id) throws ResourceNotFoundException {
		PlanningStrgyGrpResponse foundPlanningStrgyGrp = planningStrgyGrpService.getPlanningStrgyGrpById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundPlanningStrgyGrp);
	}

	@GetMapping("/getAllPlanningStrgyGrp")
	public ResponseEntity<Object> getAllPlanningStrgyGrp() {
		List<PlanningStrgyGrpResponse> planningStrgyGrp = planningStrgyGrpService.getAllPlanningStrgyGrp();
		return ResponseEntity.ok(planningStrgyGrp);
	}

	@PutMapping("/updatePlanningStrgyGrp/{id}")
	public ResponseEntity<Object> updatePlanningStrgyGrp(@PathVariable Long id,
			@RequestBody PlanningStrgyGrpRequest planningStrgyGrpRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		PlanningStrgyGrpResponse updatePlanningStrgyGrp = planningStrgyGrpService.updatePlanningStrgyGrp(id,
				planningStrgyGrpRequest);
		return ResponseEntity.ok().body(updatePlanningStrgyGrp);
	}

	@PatchMapping("/updatePlanningStrgyGrpById/{id}")
	public ResponseEntity<Object> updatePlanningStrgyGrpStatusId(@PathVariable Long id)
			throws ResourceNotFoundException {
		PlanningStrgyGrpResponse response = planningStrgyGrpService.updateStatusUsingPlanningStrgyGrpId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusPlanningStrgyGrpId/{id}")
	public ResponseEntity<Object> updateBulkStatusPlanningStrgyGrpId(@PathVariable List<Long> id)
			throws ResourceNotFoundException {
		List<PlanningStrgyGrpResponse> responseList = planningStrgyGrpService.updateBulkStatusPlanningStrgyGrpId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deletePlanningStrgyGrp/{id}")
	public ResponseEntity<String> deletePlanningStrgyGrp(@PathVariable Long id) throws ResourceNotFoundException {
		planningStrgyGrpService.deletePlanningStrgyGrp(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchPlanningStrgyGrp")
	public ResponseEntity<Object> deleteBatchPlanningStrgyGrp(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		planningStrgyGrpService.deleteBatchPlanningStrgyGrp(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplatePlanningStrategyGrp")
	public void exportExcelTemplatePlanningStrategyGrp(HttpServletResponse response) throws IOException {
		planningStrgyGrpService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataPlanningStrategyGrp", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		planningStrgyGrpService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataPlanningStrategyGrp")
	public ResponseEntity<Object> exportExcelDataPlanningStrategyGrp(HttpServletResponse response)
			throws IOException, ExcelFileException {
		planningStrgyGrpService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfPlanningStrategyGrpReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<PlanningStrategyGrp> planningStrategyGrpReport = planningStrgyGrpService.findAll();
		List<Map<String, Object>> data = planningStrgyGrpService
				.convertPlanningStrategyGrpListToMap(planningStrategyGrpReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "PlanningStrategyGrpReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "PlanningStrategyGrpReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
