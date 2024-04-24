package com.example.batchservice.controller;

import java.io.IOException;
import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.batchservice.service.PlantService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlantController {

	private final PlantService plantService;
	private final JobLauncher jobLauncher;
	private final Job job;

	@GetMapping("/downloadPlantTemplate")
	public ResponseEntity<Resource> downloadPlantTemplate(HttpServletResponse response) throws IOException {
		plantService.downloadPlantTemplate(response);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/ImportExcelDataPlant", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> importExcel(@RequestPart("file") MultipartFile file) throws Exception {
		jobLauncher.run(job, new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
				.addDate("date", new Date()).toJobParameters());
		return ResponseEntity.ok().body("Excel file imported successfully");
	}

	@GetMapping("/downloadDepartmentTemplate")
	public ResponseEntity<Resource> downloadDepartmentTemplate(HttpServletResponse response) throws IOException {
		plantService.downloadDepartmentTemplate(response);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/downloadPriceControlTemplate")
	public ResponseEntity<Resource> downloadPriceControlTemplate(HttpServletResponse response) throws IOException {
		plantService.downloadPriceControlTemplate(response);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/downloadProfitCenterTemplate")
	public ResponseEntity<Resource> downloadProfitCenterTemplate(HttpServletResponse response) throws IOException {
		plantService.downloadProfitCenterTemplate(response);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/downloadStorageBinTemplate")
	public ResponseEntity<Resource> downloadStorageBinTemplate(HttpServletResponse response) throws IOException {
		plantService.downloadStorageBinTemplate(response);
		return ResponseEntity.ok().build();
	}

}
