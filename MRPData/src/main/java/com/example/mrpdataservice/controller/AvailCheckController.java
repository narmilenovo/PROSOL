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
import com.example.mrpdataservice.entity.AvailCheck;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.AvailCheckRequest;
import com.example.mrpdataservice.response.AvailCheckResponse;
import com.example.mrpdataservice.service.AvailCheckService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AvailCheckController {

	private final AvailCheckService availCheckService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveAvailCheck")
	public ResponseEntity<Object> saveAvailCheck(@Valid @RequestBody AvailCheckRequest availCheckRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveAvailCheck").toUriString());
		AvailCheckResponse savedAvailCheck = availCheckService.saveAvailCheck(availCheckRequest);
		return ResponseEntity.created(uri).body(savedAvailCheck);
	}

	@GetMapping("/getAvailCheckById/{id}")
	public ResponseEntity<Object> getAvailCheckById(@PathVariable Long id) throws ResourceNotFoundException {
		AvailCheckResponse foundAvailCheck = availCheckService.getAvailCheckById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundAvailCheck);
	}

	@GetMapping("/getAllAvailCheck")
	public ResponseEntity<Object> getAllAvailCheck() {
		List<AvailCheckResponse> availCheck = availCheckService.getAllAvailCheck();
		return ResponseEntity.ok(availCheck);
	}

	@PutMapping("/updateAvailCheck/{id}")
	public ResponseEntity<Object> updateAvailCheck(@PathVariable Long id,
			@RequestBody AvailCheckRequest availCheckRequest) throws ResourceNotFoundException, AlreadyExistsException {
		AvailCheckResponse updateAvailCheck = availCheckService.updateAvailCheck(id, availCheckRequest);
		return ResponseEntity.ok().body(updateAvailCheck);
	}

	@PatchMapping("/updateAvailCheckById/{id}")
	public ResponseEntity<Object> updateAvailCheckStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		AvailCheckResponse response = availCheckService.updateStatusUsingAvailCheckId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusAvailCheckId/{id}")
	public ResponseEntity<Object> updateBulkStatusAvailCheckId(@PathVariable List<Long> id)
			throws ResourceNotFoundException {
		List<AvailCheckResponse> responseList = availCheckService.updateBulkStatusAvailCheckId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteAvailCheck/{id}")
	public ResponseEntity<String> deleteAvailCheck(@PathVariable Long id) throws ResourceNotFoundException {
		availCheckService.deleteAvailCheck(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchAvailCheck")
	public ResponseEntity<Object> deleteBatchAvailCheck(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		availCheckService.deleteBatchAvailCheck(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateAvailCheck")
	public void exportExcelTemplateAvailCheck(HttpServletResponse response) throws IOException {
		availCheckService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataAvailCheck", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		availCheckService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataAvailCheck")
	public ResponseEntity<Object> exportExcelDataAvailCheck(HttpServletResponse response)
			throws IOException, ExcelFileException {
		availCheckService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfAvailCheckReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<AvailCheck> availCheckReport = availCheckService.findAll();
		List<Map<String, Object>> data = availCheckService.convertDepartmentListToMap(availCheckReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "AvailCheckReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "AvailCheckReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
