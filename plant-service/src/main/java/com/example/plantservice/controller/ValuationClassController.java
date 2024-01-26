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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.plantservice.config.GeneratePdfReport;
import com.example.plantservice.dto.request.ValuationClassRequest;
import com.example.plantservice.dto.response.ValuationClassResponse;
import com.example.plantservice.entity.ValuationClass;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.ValuationClassService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ValuationClassController {

	private final ValuationClassService valuationClassService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveValuationClass")
	public ResponseEntity<Object> saveValuationClass(@Valid @RequestBody ValuationClassRequest valuationClassRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveValuationClass").toUriString());
		ValuationClassResponse savedValuationClass = valuationClassService.saveValuationClass(valuationClassRequest);
		return ResponseEntity.created(uri).body(savedValuationClass);
	}

	@GetMapping("/getValuationClassById/{id}")
	public ResponseEntity<Object> getValuationClassById(@PathVariable Long id,
			@RequestParam(required = false) Boolean showFull) throws ResourceNotFoundException {
		Object foundValuationClass;
		if (Boolean.TRUE.equals(showFull)) {
			foundValuationClass = valuationClassService.getValuationMaterialById(id);
		} else {
			foundValuationClass = valuationClassService.getValuationClassById(id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(foundValuationClass);
	}

	@GetMapping("/getAllValuationClass")
	public ResponseEntity<Object> getAllValuationClass(@RequestParam(required = false) Boolean showFull)
			throws ResourceNotFoundException {
		List<?> valuationClass;
		if (Boolean.TRUE.equals(showFull)) {
			valuationClass = valuationClassService.getAllValuationClassByMaterial();
		} else {
			valuationClass = valuationClassService.getAllValuationClass();
		}
		return ResponseEntity.ok(valuationClass);
	}

	// @GetMapping("/getAllValuationClassByMaterial")
	// public ResponseEntity<Object> getAllValuationClassByMaterial() throws
	// ResourceNotFoundException {
	// List<ValuationMaterialResponse> materialResponse =
	// valuationClassService.getAllValuationClassByMaterial();
	// return ResponseEntity.ok(materialResponse);
	// }

	@PutMapping("/updateValuationClass/{id}")
	public ResponseEntity<Object> updateValuationClass(@PathVariable Long id,
			@RequestBody ValuationClassRequest valuationClassRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		ValuationClassResponse updateValuationClass = valuationClassService.updateValuationClass(id,
				valuationClassRequest);
		return ResponseEntity.ok().body(updateValuationClass);
	}

	@PatchMapping("/updateValuationClassById/{id}")
	public ResponseEntity<Object> updateValuationClassStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		ValuationClassResponse response = valuationClassService.updateStatusUsingValuationClassId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusValuationClassId")
	public ResponseEntity<Object> updateBulkStatusValuationClassId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<ValuationClassResponse> responseList = valuationClassService.updateBulkStatusValuationClassId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteValuationClass/{id}")
	public ResponseEntity<String> deleteValuationClass(@PathVariable Long id) throws ResourceNotFoundException {
		valuationClassService.deleteValuationClass(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchValuationClass")
	public ResponseEntity<Object> deleteBatchValuationClass(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		valuationClassService.deleteBatchValuationClass(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateValuationClass")
	public void exportExcelTemplateValuationClass(HttpServletResponse response) throws IOException {
		valuationClassService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataValuationClass", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		valuationClassService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");
	}

	@GetMapping("/exportDataValuationClass")
	public ResponseEntity<Object> exportExcelDataValuationClass(HttpServletResponse response)
			throws IOException, ExcelFileException, ResourceNotFoundException {
		valuationClassService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfValuationClassReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() throws ResourceNotFoundException {
		List<ValuationClass> valuationClass = valuationClassService.findAllValuationClass();
		List<Map<String, Object>> data = valuationClassService.convertValuationClassListToMap(valuationClass);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "ValuationClassReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "ValuationClassReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
