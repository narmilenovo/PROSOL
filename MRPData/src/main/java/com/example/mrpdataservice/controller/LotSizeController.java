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
import com.example.mrpdataservice.entity.LotSize;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.request.LotSizeRequest;
import com.example.mrpdataservice.response.LotSizeResponse;
import com.example.mrpdataservice.service.LotSizeService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class LotSizeController {

	private final LotSizeService lotSizeService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveLotSize")
	public ResponseEntity<Object> saveLotSize(@Valid @RequestBody LotSizeRequest lotSizeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveLotSize").toUriString());
		LotSizeResponse savedLotSize = lotSizeService.saveLotSize(lotSizeRequest);
		return ResponseEntity.created(uri).body(savedLotSize);
	}

	@GetMapping("/getLotSizeById/{id}")
	public ResponseEntity<Object> getLotSizeById(@PathVariable Long id) throws ResourceNotFoundException {
		LotSizeResponse foundLotSize = lotSizeService.getLotSizeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundLotSize);
	}

	@GetMapping("/getAllLotSize")
	public ResponseEntity<Object> getAllLotSize() {
		List<LotSizeResponse> lotSize = lotSizeService.getAllLotSize();
		return ResponseEntity.ok(lotSize);
	}

	@PutMapping("/updateLotSize/{id}")
	public ResponseEntity<Object> updateLotSize(@PathVariable Long id, @RequestBody LotSizeRequest lotSizeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		LotSizeResponse updateLotSize = lotSizeService.updateLotSize(id, lotSizeRequest);
		return ResponseEntity.ok().body(updateLotSize);
	}

	@PatchMapping("/updateLotSizeById/{id}")
	public ResponseEntity<Object> updateLotSizeStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		LotSizeResponse response = lotSizeService.updateStatusUsingLotSizeId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusLotSizeId/{id}")
	public ResponseEntity<Object> updateBulkStatusLotSizeId(@PathVariable List<Long> id)
			throws ResourceNotFoundException {
		List<LotSizeResponse> responseList = lotSizeService.updateBulkStatusLotSizeId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteLotSize/{id}")
	public ResponseEntity<String> deleteLotSize(@PathVariable Long id) throws ResourceNotFoundException {
		lotSizeService.deleteLotSize(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchLotSize")
	public ResponseEntity<Object> deleteBatchLotSize(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		lotSizeService.deleteBatchLotSize(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateLotSize")
	public void exportExcelTemplateLotSize(HttpServletResponse response) throws IOException {
		lotSizeService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataLotSize", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		lotSizeService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataLotSize")
	public ResponseEntity<Object> exportExcelDataLotSize(HttpServletResponse response)
			throws IOException, ExcelFileException {
		lotSizeService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfLotSizeReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<LotSize> lotSizeReport = lotSizeService.findAll();
		List<Map<String, Object>> data = lotSizeService.convertLotSizeListToMap(lotSizeReport);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "LotSizeReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "LotSizeReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}

}
