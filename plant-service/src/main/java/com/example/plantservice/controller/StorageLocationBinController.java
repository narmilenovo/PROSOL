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
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.StorageBin;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PlantService;
import com.example.plantservice.service.interfaces.StorageLocationBinService;
import com.example.plantservice.service.interfaces.StorageLocationService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StorageLocationBinController {

	private final StorageLocationBinService storageLocationBinService;
	private final GeneratePdfReport generatePdfReport;
	private final PlantService plantService;
	private final StorageLocationService storageLocationService;

	@PostMapping("/saveStorageLocationBin")
	public ResponseEntity<Object> saveStorageLocationBin(@Valid @RequestBody StorageBinRequest storageBinRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveStorageLocationBin").toUriString());
		StorageBinResponse savedStorageLocationBin = storageLocationBinService.saveStorageLocation(storageBinRequest);
		return ResponseEntity.created(uri).body(savedStorageLocationBin);
	}

	@GetMapping("/getStorageLocationBinById/{id}")
	public ResponseEntity<Object> getStorageLocationBinById(@PathVariable Long id) throws ResourceNotFoundException {
		StorageBinResponse foundStorageLocationBin = storageLocationBinService.getStorageLocationBinById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundStorageLocationBin);
	}

	@GetMapping("/getAllStorageLocationBin")
	public ResponseEntity<Object> getAllStorageLocationBin() {
		List<StorageBinResponse> storageLocationBin = storageLocationBinService.getAllStorageLocationBin();
		return ResponseEntity.ok(storageLocationBin);
	}

	@GetMapping("/getPlantAll2")
	public ResponseEntity<Object> getAllStorageLocation() {
		List<PlantResponse> plants = plantService.getAllPlants();
		return ResponseEntity.ok(plants);
	}

	@GetMapping("/getAllStorageLocation1")
	public ResponseEntity<Object> getAllPlant() {
		List<StorageLocationResponse> storageLocation = storageLocationService.getAllStorageLocation();
		return ResponseEntity.ok(storageLocation);
	}

	@PutMapping("/updateStorageLocationBin/{id}")
	public ResponseEntity<Object> updateStorageLocation(@PathVariable Long id,
			@RequestBody StorageBinRequest storageBinRequest) throws ResourceNotFoundException, AlreadyExistsException {
		StorageBinResponse updateStorageLocationBin = storageLocationBinService.updateStorageLocationBin(id,
				storageBinRequest);
		return ResponseEntity.ok().body(updateStorageLocationBin);
	}

	@PatchMapping("/updateStorageLocationStatusById/{id}")
	public ResponseEntity<Object> updateStorageLocationBinStatusId(@PathVariable Long id)
			throws ResourceNotFoundException {
		StorageBinResponse response = storageLocationBinService.updateStatusUsingStorageLocationBinId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStorageLocationId")
	public ResponseEntity<Object> updateBulkStatusStorageLocationBinId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<StorageBinResponse> responseList = storageLocationBinService.updateBulkStatusStorageLocationBinId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteStorageLocationBin/{id}")
	public ResponseEntity<String> deleteStorageLocationBin(@PathVariable Long id) throws ResourceNotFoundException {
		storageLocationBinService.deleteStorageLocationBin(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchStorageLocationBin")
	public ResponseEntity<Object> deleteBatchStorageLocationBin(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		storageLocationBinService.deleteBatchStorageLocationBin(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateStorageLocationBin")
	public void exportExcelTemplateStorageLocationBin(HttpServletResponse response) throws IOException {
		storageLocationBinService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataStorageBin", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		storageLocationBinService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");
	}

	@GetMapping("/exportDataStorageLocationBin")
	public ResponseEntity<Object> exportExcelDataStorageLocationBin(HttpServletResponse response)
			throws IOException, ExcelFileException {
		storageLocationBinService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfStorageBinReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<StorageBin> bin = storageLocationBinService.findAll();
		List<Map<String, Object>> data = storageLocationBinService.convertBinListToMap(bin);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "StorageLocationBinReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "StorageLocationBinReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}

}
