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
import com.example.mrpdataservice.entity.ScheduleMargin;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.ScheduleMarginRequest;
import com.example.mrpdataservice.response.ScheduleMarginResponse;
import com.example.mrpdataservice.service.ScheduleMarginService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ScheduleMarginController {
	private final GeneratePdfReport generatePdfReport;
	private final ScheduleMarginService scheduleMarginService;

	@PostMapping("/saveScheduleMargin")
	public ResponseEntity<Object> saveScheduleMargin(@Valid @RequestBody ScheduleMarginRequest scheduleMarginRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveScheduleMargin").toUriString());
		ScheduleMarginResponse savedScheduleMargin = scheduleMarginService.saveScheduleMargin(scheduleMarginRequest);
		return ResponseEntity.created(uri).body(savedScheduleMargin);
	}

	@GetMapping("/getScheduleMarginById/{id}")
	public ResponseEntity<Object> getScheduleMarginById(@PathVariable Long id) throws ResourceNotFoundException {
		ScheduleMarginResponse foundScheduleMargin = scheduleMarginService.getScheduleMarginById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundScheduleMargin);
	}

	@GetMapping("/getAllScheduleMargin")
	public ResponseEntity<Object> getAllScheduleMargin() {
		List<ScheduleMarginResponse> scheduleMargin = scheduleMarginService.getAllScheduleMargin();
		return ResponseEntity.ok(scheduleMargin);
	}

	@PutMapping("/updateScheduleMargin/{id}")
	public ResponseEntity<Object> updateScheduleMargin(@PathVariable Long id,
			@RequestBody ScheduleMarginRequest scheduleMarginRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		ScheduleMarginResponse updateScheduleMargin = scheduleMarginService.updateScheduleMargin(id,
				scheduleMarginRequest);
		return ResponseEntity.ok().body(updateScheduleMargin);
	}

	@PatchMapping("/updateScheduleMarginById/{id}")
	public ResponseEntity<Object> updateScheduleMarginStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		ScheduleMarginResponse response = scheduleMarginService.updateStatusUsingScheduleMarginId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusScheduleMarginId")
	public ResponseEntity<Object> updateBulkStatusScheduleMarginId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<ScheduleMarginResponse> responseList = scheduleMarginService.updateBulkStatusScheduleMarginId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteScheduleMargin/{id}")
	public ResponseEntity<String> deleteScheduleMargin(@PathVariable Long id) throws ResourceNotFoundException {
		scheduleMarginService.deleteScheduleMargin(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchScheduleMargin")
	public ResponseEntity<Object> deleteBatchScheduleMargin(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		scheduleMarginService.deleteBatchScheduleMargin(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateScheduleMargin")
	public void exportExcelTemplateScheduleMargin(HttpServletResponse response) throws IOException {
		scheduleMarginService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataScheduleMargin", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		scheduleMarginService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataScheduleMargin")
	public ResponseEntity<Object> exportExcelDataScheduleMargin(HttpServletResponse response)
			throws IOException, ExcelFileException {
		scheduleMarginService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfScheduleMarginReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<ScheduleMargin> scheduleMarginReport = scheduleMarginService.findAll();
		List<Map<String, Object>> data = scheduleMarginService.convertScheduleMarginListToMap(scheduleMarginReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "ScheduleMarginReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "ScheduleMarginReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
