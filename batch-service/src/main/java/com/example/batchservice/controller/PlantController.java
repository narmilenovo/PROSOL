package com.example.batchservice.controller;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.batchservice.service.PlantService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PlantController {

	private final PlantService plantService;

	@GetMapping("/downloadPlantTemplate")
	public ResponseEntity<Resource> downloadPlantTemplate(HttpServletResponse response) throws IOException {
		plantService.downloadPlantTemplate(response);
		return ResponseEntity.ok().build();
	}

}
