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
import com.example.mrpdataservice.entity.ProcurementType;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.ProcurementTypeRequest;
import com.example.mrpdataservice.response.ProcurementTypeResponse;
import com.example.mrpdataservice.service.ProcurementTypeService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProcurementTypeController {
	private final GeneratePdfReport generatePdfReport;
	private final ProcurementTypeService procurementTypeService;

	@PostMapping("/saveProcurementType")
	public ResponseEntity<Object> saveProcurementType(@Valid @RequestBody ProcurementTypeRequest procurementTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveProcurementType").toUriString());
		ProcurementTypeResponse savedProcurementType = procurementTypeService
				.saveProcurementType(procurementTypeRequest);
		return ResponseEntity.created(uri).body(savedProcurementType);
	}

	@GetMapping("/getProcurementTypeById/{id}")
	public ResponseEntity<Object> getProcurementTypeById(@PathVariable Long id) throws ResourceNotFoundException {
		ProcurementTypeResponse foundProcurementType = procurementTypeService.getProcurementTypeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundProcurementType);
	}

	@GetMapping("/getAllProcurementType")
	public ResponseEntity<Object> getAllProcurementType() {
		List<ProcurementTypeResponse> procurementType = procurementTypeService.getAllProcurementType();
		return ResponseEntity.ok(procurementType);
	}

	@PutMapping("/updateProcurementType/{id}")
	public ResponseEntity<Object> updateProcurementType(@PathVariable Long id,
			@RequestBody ProcurementTypeRequest procurementTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		ProcurementTypeResponse updateProcurementType = procurementTypeService.updateProcurementType(id,
				procurementTypeRequest);
		return ResponseEntity.ok().body(updateProcurementType);
	}

	@PatchMapping("/updateProcurementTypeById/{id}")
	public ResponseEntity<Object> updateProcurementTypeStatusId(@PathVariable Long id)
			throws ResourceNotFoundException {
		ProcurementTypeResponse response = procurementTypeService.updateStatusUsingProcurementTypeId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusProcurementTypeId/{id}")
	public ResponseEntity<Object> updateBulkStatusProcurementTypeId(@PathVariable List<Long> id)
			throws ResourceNotFoundException {
		List<ProcurementTypeResponse> responseList = procurementTypeService.updateBulkStatusProcurementTypeId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteProcurementType/{id}")
	public ResponseEntity<String> deleteProcurementType(@PathVariable Long id) throws ResourceNotFoundException {
		procurementTypeService.deleteProcurementType(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchProcurementType")
	public ResponseEntity<Object> deleteBatchProcurementType(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		procurementTypeService.deleteBatchProcurementType(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateProcurementType")
	public void exportExcelTemplateProcurementType(HttpServletResponse response) throws IOException {
		procurementTypeService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataProcurementType", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		procurementTypeService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataProcurementType")
	public ResponseEntity<Object> exportExcelDataProcurementType(HttpServletResponse response)
			throws IOException, ExcelFileException {
		procurementTypeService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfProcurementTypeReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<ProcurementType> procurementTypeReport = procurementTypeService.findAll();
		List<Map<String, Object>> data = procurementTypeService.convertProcurementTypeListToMap(procurementTypeReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "ProcurementTypeReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "ProcurementTypeReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}

}
