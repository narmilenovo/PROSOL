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
import com.example.plantservice.dto.request.StorageBinRequest;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.StorageBin;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.StorageBinService;
import com.example.plantservice.service.interfaces.StorageLocationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StorageBinController {

	private final StorageBinService storageBinService;
	private final GeneratePdfReport generatePdfReport;
	private final StorageLocationService storageLocationService;

	@PostMapping("/saveStorageBin")
	public ResponseEntity<Object> saveStorageBin(@Valid @RequestBody StorageBinRequest storageBinRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveStorageBin").toUriString());
		StorageBinResponse savedStorageBin = storageBinService.saveStorageBin(storageBinRequest);
		return ResponseEntity.created(uri).body(savedStorageBin);
	}

	@GetMapping("/getStorageBinById/{id}")
	public ResponseEntity<Object> getStorageBinById(@PathVariable Long id) throws ResourceNotFoundException {
		StorageBinResponse foundStorageBin = storageBinService.getStorageBinById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundStorageBin);
	}

	@GetMapping("/getAllStorageBin")
	public ResponseEntity<Object> getAllStorageBin() {
		List<StorageBinResponse> storageBin = storageBinService.getAllStorageBin();
		return ResponseEntity.ok(storageBin);
	}

	// @GetMapping("/getPlantAll2")
	// public ResponseEntity<Object> getAllStorageLocation() {
	// List<PlantResponse> plants = plantService.getAllPlants();
	// return ResponseEntity.ok(plants);
	// }

	@GetMapping("/getAllStorageLocation1")
	public ResponseEntity<Object> getAllPlant() {
		List<StorageLocationResponse> storageLocation = storageLocationService.getAllStorageLocation();
		return ResponseEntity.ok(storageLocation);
	}

	@PutMapping("/updateStorageBin/{id}")
	public ResponseEntity<Object> updateStorageLocation(@PathVariable Long id,
			@RequestBody StorageBinRequest storageBinRequest) throws ResourceNotFoundException, AlreadyExistsException {
		StorageBinResponse updateStorageBin = storageBinService.updateStorageBin(id, storageBinRequest);
		return ResponseEntity.ok().body(updateStorageBin);
	}

	@PatchMapping("/updateStorageBinStatusById/{id}")
	public ResponseEntity<Object> updateStorageBinStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		StorageBinResponse response = storageBinService.updateStatusUsingStorageBinId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStorageBinId")
	public ResponseEntity<Object> updateBulkStatusStorageBinId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<StorageBinResponse> responseList = storageBinService.updateBulkStatusStorageBinId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteStorageBin/{id}")
	public ResponseEntity<String> deleteStorageBin(@PathVariable Long id) throws ResourceNotFoundException {
		storageBinService.deleteStorageBin(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchStorageBin")
	public ResponseEntity<Object> deleteBatchStorageBin(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		storageBinService.deleteBatchStorageBin(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateStorageBin")
	public void exportExcelTemplateStorageBin(HttpServletResponse response) throws IOException {
		storageBinService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataStorageBin", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		storageBinService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");
	}

	@GetMapping("/exportDataStorageBin")
	public ResponseEntity<Object> exportExcelDataStorageBin(HttpServletResponse response)
			throws IOException, ExcelFileException {
		storageBinService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfStorageBinReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<StorageBin> bin = storageBinService.findAll();
		List<Map<String, Object>> data = storageBinService.convertBinListToMap(bin);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "StorageBinReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "StorageBinReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}

}
