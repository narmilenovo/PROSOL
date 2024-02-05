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
import com.example.mrpdataservice.entity.MrpType;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.MrpTypeRequest;
import com.example.mrpdataservice.response.MrpTypeResponse;
import com.example.mrpdataservice.service.MrpTypeService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MrpTypeController {
	private final GeneratePdfReport generatePdfReport;
	private final MrpTypeService mrpTypeService;

	@PostMapping("/saveMrpType")
	public ResponseEntity<Object> saveMrpType(@Valid @RequestBody MrpTypeRequest mrpTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveMrpType").toUriString());
		MrpTypeResponse savedMrpType = mrpTypeService.saveMrpType(mrpTypeRequest);
		return ResponseEntity.created(uri).body(savedMrpType);
	}

	@GetMapping("/getMrpTypeById/{id}")
	public ResponseEntity<Object> getMrpTypeById(@PathVariable Long id) throws ResourceNotFoundException {
		MrpTypeResponse foundMrpType = mrpTypeService.getMrpTypeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundMrpType);
	}

	@GetMapping("/getAllMrpType")
	public ResponseEntity<Object> getAllMrpType() {
		List<MrpTypeResponse> mrpType = mrpTypeService.getAllMrpType();
		return ResponseEntity.ok(mrpType);
	}

	@PutMapping("/updateMrpType/{id}")
	public ResponseEntity<Object> updateMrpType(@PathVariable Long id, @RequestBody MrpTypeRequest mrpTypeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		MrpTypeResponse updateMrpType = mrpTypeService.updateMrpType(id, mrpTypeRequest);
		return ResponseEntity.ok().body(updateMrpType);
	}

	@PatchMapping("/updateMrpTypeById/{id}")
	public ResponseEntity<Object> updateMrpTypeStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		MrpTypeResponse response = mrpTypeService.updateStatusUsingMrpTypeId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusMrpTypeId")
	public ResponseEntity<Object> updateBulkStatusMrpTypeId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<MrpTypeResponse> responseList = mrpTypeService.updateBulkStatusMrpTypeId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteMrpType/{id}")
	public ResponseEntity<String> deleteMrpType(@PathVariable Long id) throws ResourceNotFoundException {
		mrpTypeService.deleteMrpType(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchMrpType")
	public ResponseEntity<Object> deleteBatchMrpType(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		mrpTypeService.deleteBatchMrpType(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateMrpType")
	public void exportExcelTemplateMrpType(HttpServletResponse response) throws IOException {
		mrpTypeService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataMrpType", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		mrpTypeService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataMrpType")
	public ResponseEntity<Object> exportExcelDataMrpType(HttpServletResponse response)
			throws IOException, ExcelFileException {
		mrpTypeService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfMrpTypeReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<MrpType> mrpControlReport = mrpTypeService.findAll();
		List<Map<String, Object>> data = mrpTypeService.convertMrpTypeListToMap(mrpControlReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "MrpTypeReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "MrpTypeReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
