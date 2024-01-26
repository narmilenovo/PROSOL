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
import com.example.plantservice.dto.request.PriceControlRequest;
import com.example.plantservice.dto.response.PriceControlResponse;
import com.example.plantservice.entity.PriceControl;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PriceControlService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PriceControlController {

	private final PriceControlService priceControlService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/savePriceControl")
	public ResponseEntity<Object> savePriceControl(@Valid @RequestBody PriceControlRequest priceControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePriceControl").toUriString());
		PriceControlResponse savedPriceControl = priceControlService.savePriceControl(priceControlRequest);
		return ResponseEntity.created(uri).body(savedPriceControl);
	}

	@GetMapping("/getPriceControlById/{id}")
	public ResponseEntity<Object> getPriceControlById(@PathVariable Long id) throws ResourceNotFoundException {
		PriceControlResponse foundPriceControl = priceControlService.getPriceControlById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundPriceControl);
	}

	@GetMapping("/getAllPriceControl")
	public ResponseEntity<Object> getAllPriceControl() {
		List<PriceControlResponse> priceControls = priceControlService.getAllPriceControl();
		return ResponseEntity.ok(priceControls);
	}

	@PutMapping("/updatePriceControl/{id}")
	public ResponseEntity<Object> updatePriceControl(@PathVariable Long id,
			@RequestBody PriceControlRequest priceControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		PriceControlResponse updatePriceControl = priceControlService.updatePriceControl(id, priceControlRequest);
		return ResponseEntity.ok().body(updatePriceControl);
	}

	@PatchMapping("/updatePriceControlById/{id}")
	public ResponseEntity<Object> updatePriceControlStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		PriceControlResponse response = priceControlService.updateStatusUsingPriceControlId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusPriceControlId")
	public ResponseEntity<Object> updateBulkStatusPriceControlId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<PriceControlResponse> responseList = priceControlService.updateBulkStatusPriceControlId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deletePriceControl/{id}")
	public ResponseEntity<String> deletePriceControl(@PathVariable Long id) throws ResourceNotFoundException {
		priceControlService.deletePriceControl(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchPriceControl")
	public ResponseEntity<Object> deleteBatchPriceControl(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		priceControlService.deleteBatchPriceControl(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplatePrice")
	public void exportExcelTemplatePrice(HttpServletResponse response) throws IOException {
		priceControlService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataPrice", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		priceControlService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataPrice")
	public ResponseEntity<Object> exportExcelDataPrice(HttpServletResponse response)
			throws IOException, ExcelFileException {
		priceControlService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfPriceControlReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<PriceControl> price = priceControlService.findAll();
		List<Map<String, Object>> data = priceControlService.convertPriceListToMap(price);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "PriceControlReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "PriceControlReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}

}
