package com.example.plantservice.controller;

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

import com.example.plantservice.config.GeneratePdfReport;
import com.example.plantservice.dto.request.ValuationCategoryRequest;
import com.example.plantservice.dto.response.ValuationCategoryResponse;
import com.example.plantservice.entity.ValuationCategory;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.ValuationCategoryService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ValuationCategoryController {

	private final ValuationCategoryService valuationCategoryService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveValuationCategory")
	public ResponseEntity<Object> saveValuationCategory(
			@Valid @RequestBody ValuationCategoryRequest valuationCategoryRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveValuationCategory").toUriString());
		ValuationCategoryResponse savedValuationCategory = valuationCategoryService
				.saveValuationCategory(valuationCategoryRequest);
		return ResponseEntity.created(uri).body(savedValuationCategory);
	}

	@GetMapping("/getValuationCategoryById/{id}")
	public ResponseEntity<Object> getValuationCategoryById(@PathVariable Long id) throws ResourceNotFoundException {
		ValuationCategoryResponse foundValuationCategory = valuationCategoryService.getValuationCategoryById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundValuationCategory);
	}

	@GetMapping("/getAllValuationCategory")
	public ResponseEntity<Object> getAllValuationCategory() {
		List<ValuationCategoryResponse> valuationCategory = valuationCategoryService.getAllValuationCategory();
		return ResponseEntity.ok(valuationCategory);
	}

	@PutMapping("/updateValuationCategory/{id}")
	public ResponseEntity<Object> updateValuationCategory(@PathVariable Long id,
			@RequestBody ValuationCategoryRequest valuationCategoryRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		ValuationCategoryResponse updateValuationCategory = valuationCategoryService.updateValuationCategory(id,
				valuationCategoryRequest);
		return ResponseEntity.ok().body(updateValuationCategory);
	}

	@PatchMapping("/updateValuationCategoryById/{id}")
	public ResponseEntity<Object> updateValuationCategoryStatusId(@PathVariable Long id)
			throws ResourceNotFoundException {
		ValuationCategoryResponse response = valuationCategoryService.updateStatusUsingValuationCategoryId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusValuationCategoryId")
	public ResponseEntity<Object> updateBulkStatusValuationCategoryId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<ValuationCategoryResponse> responseList = valuationCategoryService.updateBulkStatusValuationCategoryId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteValuationCategory/{id}")
	public ResponseEntity<String> deleteValuationCategory(@PathVariable Long id) throws ResourceNotFoundException {
		valuationCategoryService.deleteValuationCategory(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchValuationCategory")
	public ResponseEntity<Object> deleteBatchValuationCategory(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		valuationCategoryService.deleteBatchValuationCategory(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateValuationCategory")
	public void exportExcelTemplateValuationCategory(HttpServletResponse response) throws IOException {
		valuationCategoryService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataValuationCategory", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		valuationCategoryService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataValuationCategory")
	public ResponseEntity<Object> exportExcelDataValuationCategory(HttpServletResponse response)
			throws IOException, ExcelFileException {
		valuationCategoryService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfValuationCategoryReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<ValuationCategory> valuationCategory = valuationCategoryService.findAll();
		List<Map<String, Object>> data = valuationCategoryService.convertValuationCategoryListToMap(valuationCategory);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "ValuationCategoryReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "ValuationCategoryReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
