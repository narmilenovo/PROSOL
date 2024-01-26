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

import com.example.mrpdataservice.client.MrpPlantResponse;
import com.example.mrpdataservice.configuration.GeneratePdfReport;
import com.example.mrpdataservice.entity.MrpControl;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.MrpControlRequest;
import com.example.mrpdataservice.response.MrpControlResponse;
import com.example.mrpdataservice.service.MrpControlService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MrpControlController {
	private final MrpControlService mrpControlService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveMrpControl")
	public ResponseEntity<Object> saveMrpControl(@Valid @RequestBody MrpControlRequest mrpControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveMrpControl").toUriString());
		MrpControlResponse savedMrpControl = mrpControlService.saveMrpControl(mrpControlRequest);
		return ResponseEntity.created(uri).body(savedMrpControl);
	}

	@GetMapping("/getMrpControlById/{id}")
	public ResponseEntity<Object> getMrpControlById(@PathVariable Long id) throws ResourceNotFoundException {
		MrpControlResponse foundMrpControl = mrpControlService.getMrpControlById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundMrpControl);
	}

	@GetMapping("/getMrpControlByName/{name}")
	public ResponseEntity<Object> getMrpControlByName(@PathVariable String name) throws ResourceNotFoundException {
		MrpControlResponse foundMrpControl = mrpControlService.getMrpControlByName(name);
		return ResponseEntity.status(HttpStatus.OK).body(foundMrpControl);
	}

	@GetMapping("/getAllMrpControl")
	public ResponseEntity<Object> getAllMrpControl() {
		List<MrpControlResponse> mrpControl = mrpControlService.getAllMrpControl();
		return ResponseEntity.ok(mrpControl);
	}

	@GetMapping("/getAllMrpControlByPlant")
	public ResponseEntity<Object> getAllMrpControlByPlant() throws ResourceNotFoundException {
		List<MrpPlantResponse> plantResponse = mrpControlService.getAllMrpControlByPlant();
		return ResponseEntity.ok(plantResponse);
	}

	@PutMapping("/updateMrpControl/{id}")
	public ResponseEntity<Object> updateMrpControl(@PathVariable Long id,
			@RequestBody MrpControlRequest mrpControlRequest) throws ResourceNotFoundException, AlreadyExistsException {
		MrpControlResponse updateMrpControl = mrpControlService.updateMrpControl(id, mrpControlRequest);
		return ResponseEntity.ok().body(updateMrpControl);
	}

	@PatchMapping("/updateMrpControlById/{id}")
	public ResponseEntity<Object> updateMrpControlStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		MrpControlResponse response = mrpControlService.updateStatusUsingMrpControlId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusMrpControlId/{id}")
	public ResponseEntity<Object> updateBulkStatusMrpControlId(@PathVariable List<Long> id)
			throws ResourceNotFoundException {
		List<MrpControlResponse> responseList = mrpControlService.updateBulkStatusMrpControlId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteMrpControl/{id}")
	public ResponseEntity<String> deleteMrpControl(@PathVariable Long id) throws ResourceNotFoundException {
		mrpControlService.deleteMrpControl(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchMrpControl")
	public ResponseEntity<Object> deleteBatchMrpControl(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		mrpControlService.deleteBatchMrpControl(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateMrpControl")
	public void exportExcelTemplateMrpControl(HttpServletResponse response) throws IOException {
		mrpControlService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataMrpControl", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		mrpControlService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataMrpControl")
	public ResponseEntity<Object> exportExcelDataMrpControl(HttpServletResponse response)
			throws IOException, ExcelFileException {
		mrpControlService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfMrpControlReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<MrpControl> mrpControlReport = mrpControlService.findAll();
		List<Map<String, Object>> data = mrpControlService.convertMrpControlListToMap(mrpControlReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "MrpControlReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "MrpControlReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
