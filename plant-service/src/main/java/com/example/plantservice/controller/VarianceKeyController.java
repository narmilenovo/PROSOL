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
import com.example.plantservice.dto.request.VarianceKeyRequest;
import com.example.plantservice.dto.response.VarianceKeyResponse;
import com.example.plantservice.entity.VarianceKey;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.VarianceKeyService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VarianceKeyController {

	private final VarianceKeyService varianceKeyService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveVarianceKey")
	public ResponseEntity<Object> saveVarianceKey(@Valid @RequestBody VarianceKeyRequest varianceKeyRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveVarianceKey").toUriString());
		VarianceKeyResponse savedVarianceKey = varianceKeyService.saveVarianceKey(varianceKeyRequest);
		return ResponseEntity.created(uri).body(savedVarianceKey);
	}

	@GetMapping("/getVarianceKeyById/{id}")
	public ResponseEntity<Object> getVarianceKeyById(@PathVariable Long id) throws ResourceNotFoundException {
		VarianceKeyResponse foundVarianceKey = varianceKeyService.getVarianceKeyById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundVarianceKey);
	}

	@GetMapping("/getAllVarianceKey")
	public ResponseEntity<Object> getAllVarianceKey() {
		List<VarianceKeyResponse> varianceKey = varianceKeyService.getAllVarianceKey();
		return ResponseEntity.ok(varianceKey);
	}

	@PutMapping("/updateVarianceKey/{id}")
	public ResponseEntity<Object> updateVarianceKey(@PathVariable Long id,
			@RequestBody VarianceKeyRequest varianceKeyRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		VarianceKeyResponse updateVarianceKey = varianceKeyService.updateVarianceKey(id, varianceKeyRequest);
		return ResponseEntity.ok().body(updateVarianceKey);
	}

	@PatchMapping("/updateVarianceKeyById/{id}")
	public ResponseEntity<Object> updateVarianceKeyStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		VarianceKeyResponse response = varianceKeyService.updateStatusUsingVarianceKeyId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusVarianceKeyId")
	public ResponseEntity<Object> updateBulkStatusVarianceKeyId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<VarianceKeyResponse> responseList = varianceKeyService.updateBulkStatusVarianceKeyId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteVarianceKey/{id}")
	public ResponseEntity<String> deleteVarianceKey(@PathVariable Long id) throws ResourceNotFoundException {
		varianceKeyService.deleteVarianceKey(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteVarianceKey")
	public ResponseEntity<Object> deleteBatchVarianceKey(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		varianceKeyService.deleteBatchVarianceKey(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateVarianceKey")
	public void exportExcelTemplateVarianceKey(HttpServletResponse response) throws IOException {
		varianceKeyService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataVarianceKey", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		varianceKeyService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataVarianceKey")
	public ResponseEntity<Object> exportExcelDataVarianceKey(HttpServletResponse response)
			throws IOException, ExcelFileException {
		varianceKeyService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfVarianceKeyReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<VarianceKey> varianceKey = varianceKeyService.findAll();
		List<Map<String, Object>> data = varianceKeyService.convertVarianceKeyListToMap(varianceKey);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "VarianceKeyReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "VarianceKeyReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
