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
import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PlantService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlantController {

	private final PlantService plantService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/savePlant")
	public ResponseEntity<Object> savePlant(@Valid @RequestBody PlantRequest plantRequest)
			throws ResourceNotFoundException, AlreadyExistsException, IllegalAccessException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePlant").toUriString());
		PlantResponse savedPlant = plantService.savePlant(plantRequest);
		return ResponseEntity.created(uri).body(savedPlant);
	}

	@GetMapping("/getPlantById/{plantId}")
	public ResponseEntity<Object> getPlantById(@PathVariable Long plantId) throws ResourceNotFoundException {
		PlantResponse foundPlant = plantService.getPlantById(plantId);
		return ResponseEntity.status(HttpStatus.OK).body(foundPlant);
	}

	@GetMapping("/getPlantByName/{name}")
	public ResponseEntity<Object> getPlantByName(@PathVariable String name) throws ResourceNotFoundException {
		PlantResponse foundPlant = plantService.getPlantByName(name);
		return ResponseEntity.status(HttpStatus.OK).body(foundPlant);
	}

	@GetMapping("/getAllPlant")
	public ResponseEntity<Object> getAllPlants() {
		List<PlantResponse> plants = plantService.getAllPlants();
		return ResponseEntity.ok(plants);
	}

	@PutMapping("/updatePlant/{id}")
	public ResponseEntity<Object> updatePlant(@PathVariable Long id, @RequestBody PlantRequest plantRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		PlantResponse updatePlant = plantService.updatePlant(id, plantRequest);
		return ResponseEntity.ok().body(updatePlant);
	}

	@PatchMapping("/updatePlantStatusById/{id}")
	public ResponseEntity<Object> updatePlantStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		PlantResponse response = plantService.updateStatusUsingPlantId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusPlantId")
	public ResponseEntity<Object> updateBulkStatusPlantId(@RequestBody List<Long> id) throws ResourceNotFoundException {
		List<PlantResponse> responseList = plantService.updateBulkStatusPlantId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deletePlant/{id}")
	public ResponseEntity<String> deletePlant(@PathVariable Long id) throws ResourceNotFoundException {
		plantService.deletePlant(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchPlant")
	public ResponseEntity<Object> deleteBatchPlant(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		plantService.deleteBatchPlant(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplatePlant")
	public void exportExcelTemplatePlant(HttpServletResponse response) throws IOException {
		plantService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataPlant", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		plantService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataPlant")
	public ResponseEntity<Object> exportExcelDataPlant(HttpServletResponse response)
			throws IOException, ExcelFileException {
		plantService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfPlantReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<Plant> plant = plantService.findAll();
		List<Map<String, Object>> data = plantService.convertPlantListToMap(plant);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "PlantReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "PlantReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}

}
