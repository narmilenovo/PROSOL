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
import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.StorageLocation;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PlantService;
import com.example.plantservice.service.interfaces.StorageLocationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StorageLocationController {

	private final StorageLocationService storageLocationService;
	private final GeneratePdfReport generatePdfReport;
	private final PlantService plantService;

	@PostMapping("/saveStorageLocation")
	public ResponseEntity<Object> saveStorageLocation(@Valid @RequestBody StorageLocationRequest storageLocationRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveStorageLocation").toUriString());
		StorageLocationResponse savedStorageLocation = storageLocationService
				.saveStorageLocation(storageLocationRequest);
		return ResponseEntity.created(uri).body(savedStorageLocation);
	}

	@GetMapping("/getStorageLocationById/{id}")
	public ResponseEntity<Object> getStorageLocationById(@PathVariable Long id) throws ResourceNotFoundException {
		StorageLocationResponse foundStorageLocation = storageLocationService.getStorageLocationById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundStorageLocation);
	}

	@GetMapping("/getAllStorageLocation")
	public ResponseEntity<Object> getAllStorageLocation() {
		List<StorageLocationResponse> storageLocation = storageLocationService.getAllStorageLocation();
		return ResponseEntity.ok(storageLocation);
	}

	@GetMapping("/getPlantAll3")
	public ResponseEntity<Object> getAllPlant() {
		List<PlantResponse> plants = plantService.getAllPlants();
		return ResponseEntity.ok(plants);
	}

	@GetMapping("/getAllByPlantByName/{name}")
	public ResponseEntity<Object> getAllByPlantByName(@PathVariable String name) {
		List<StorageLocationResponse> plantResponses = storageLocationService.getAllByPlantByName(name);
		return new ResponseEntity<>(plantResponses, HttpStatus.OK);
	}

	@GetMapping("/getAllByPlantById/{id}")
	public ResponseEntity<Object> getAllByPlantById(@PathVariable Long id) {
		List<StorageLocationResponse> plantResponses = storageLocationService.getAllByPlantById(id);
		return new ResponseEntity<>(plantResponses, HttpStatus.OK);
	}

	@PutMapping("/updateStorageLocation/{id}")
	public ResponseEntity<Object> updateStorageLocation(@PathVariable Long id,
			@RequestBody StorageLocationRequest storageLocationRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		StorageLocationResponse updateStorageLocation = storageLocationService.updateStorageLocation(id,
				storageLocationRequest);
		return ResponseEntity.ok().body(updateStorageLocation);
	}

	@PatchMapping("/updateStorageLocationStatusById1/{id}")
	public ResponseEntity<Object> updateStorageLocationStatusId(@PathVariable Long id)
			throws ResourceNotFoundException {
		StorageLocationResponse response = storageLocationService.updateStatusUsingStorageLocationId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStorageLocationId1")
	public ResponseEntity<Object> updateBulkStatusStorageLocationId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<StorageLocationResponse> responseList = storageLocationService.updateBulkStatusStorageLocationId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteStorageLocation/{id}")
	public ResponseEntity<String> deleteStorageLocation(@PathVariable Long id) throws ResourceNotFoundException {
		storageLocationService.deleteStorageLocation(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchStorageLocation")
	public ResponseEntity<Object> deleteBatchStorageLocation(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		storageLocationService.deleteBatchStorageLocation(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateStorageLocation")
	public void exportExcelTemplateStorageLocation(HttpServletResponse response) throws IOException {
		storageLocationService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataStorageLocation", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		storageLocationService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataStorageLocation")
	public ResponseEntity<Object> exportExcelDataStorageLocation(HttpServletResponse response)
			throws IOException, ExcelFileException {
		storageLocationService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfStorageLocationReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<StorageLocation> storage = storageLocationService.findAll();
		List<Map<String, Object>> data = storageLocationService.convertStorageLocationListToMap(storage);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "StorageLocationReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "StorageLocationReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
