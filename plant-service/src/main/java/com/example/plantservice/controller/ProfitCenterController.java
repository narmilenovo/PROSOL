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
import com.example.plantservice.dto.request.ProfitCenterRequest;
import com.example.plantservice.dto.response.ProfitCenterResponse;
import com.example.plantservice.entity.ProfitCenter;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.ProfitCenterService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProfitCenterController {

	private final ProfitCenterService profitCenterService;
	private final GeneratePdfReport generatePdfReport;

	@PostMapping("/saveProfitCenter")
	public ResponseEntity<Object> saveProfitCenter(@Valid @RequestBody ProfitCenterRequest profitCenterRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		URI uri = URI
				.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveProfitCenter").toUriString());
		ProfitCenterResponse savedProfitCenter = profitCenterService.saveProfitCenter(profitCenterRequest);
		return ResponseEntity.created(uri).body(savedProfitCenter);
	}

	@GetMapping("/getProfitCenterById/{id}")
	public ResponseEntity<Object> getProfitCenterById(@PathVariable Long id) throws ResourceNotFoundException {
		ProfitCenterResponse foundProfitCenter = profitCenterService.getProfitCenterById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundProfitCenter);
	}

	@GetMapping("/getAllProfitCenter")
	public ResponseEntity<Object> getAllProfitCenter() {
		List<ProfitCenterResponse> profitCenters = profitCenterService.getAllProfitCenter();
		return ResponseEntity.ok(profitCenters);
	}

//	@GetMapping("/getPlantAll1")
//	public ResponseEntity<Object> getAllPlant() {
//		List<PlantResponse> plants = plantService.getAllPlants();
//		return ResponseEntity.ok().body(plants);
//	}

	@PutMapping("/updateProfitCenter/{id}")
	public ResponseEntity<Object> updateProfitCenter(@PathVariable Long id,
			@RequestBody ProfitCenterRequest profitCenterRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		ProfitCenterResponse updateprofitCenter = profitCenterService.updateProfitCenter(id, profitCenterRequest);
		return ResponseEntity.ok().body(updateprofitCenter);
	}

	@PatchMapping("/updateProfitCenterStatusById/{id}")
	public ResponseEntity<Object> updateProfitCenterStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		ProfitCenterResponse response = profitCenterService.updateStatusUsingProfitCenterId(id);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/updateBulkStatusProfitCentertId")
	public ResponseEntity<Object> updateBulkStatusProfitCenterId(@RequestBody List<Long> id)
			throws ResourceNotFoundException {
		List<ProfitCenterResponse> responseList = profitCenterService.updateBulkStatusProfitCenterId(id);
		return ResponseEntity.ok(responseList);
	}

	@DeleteMapping("/deleteProfitCenter/{id}")
	public ResponseEntity<String> deleteProfitCenter(@PathVariable Long id) throws ResourceNotFoundException {
		profitCenterService.deleteProfitCenter(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchProfitCenter")
	public ResponseEntity<Object> deleteBatchProfitCenter(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		profitCenterService.deleteBatchProfitCenter(ids);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/exportTemplateProfit")
	public void exportExcelTemplateProfit(HttpServletResponse response) throws IOException {
		profitCenterService.downloadTemplate(response);
	}

	@PostMapping(value = "/ImportExcelDataProfit", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file)
			throws IOException, ExcelFileException, AlreadyExistsException {
		profitCenterService.importExcelSave(file);
		return ResponseEntity.ok().body("Excel file Save successfully");

	}

	@GetMapping("/exportDataProfit")
	public ResponseEntity<Object> exportExcelDataProfit(HttpServletResponse response)
			throws IOException, ExcelFileException {
		profitCenterService.downloadAllData(response);
		return ResponseEntity.ok().body("Excel file exported successfully");
	}

	@GetMapping(value = "/pdfProfitCenterReport", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> generateReport() {
		List<ProfitCenter> profit = profitCenterService.findAll();
		List<Map<String, Object>> data = profitCenterService.convertProfitListToMap(profit);
		byte[] pdfContents = generatePdfReport.generateGenericPdfReport(data, "ProfitCenterReport.pdf");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE));
		headers.setContentDispositionFormData("attachment", "ProfitCenterReport.pdf");
		return ResponseEntity.ok().headers(headers).body(pdfContents);
	}
}
