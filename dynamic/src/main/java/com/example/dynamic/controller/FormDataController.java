package com.example.dynamic.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.dynamic.dto.request.FormDataRequest;
import com.example.dynamic.dto.response.FormDataResponse;
import com.example.dynamic.exceptions.ResourceFoundException;
import com.example.dynamic.exceptions.ResourceNotFoundException;
import com.example.dynamic.service.interfaces.FormDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FormDataController {

	private final FormDataService formDataService;

	@PostMapping(value = "/{formName}/submitFormData", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> submitFormData(@PathVariable String formName,
			@Parameter(name = "formDataRequest", required = true, schema = @Schema(implementation = FormDataRequest.class), description = "source") @RequestParam String source,
			@RequestParam(required = false) MultiValueMap<String, MultipartFile> dynamicFiles)
			throws ResourceNotFoundException, ResourceFoundException, JsonProcessingException {
		FormDataRequest formDataRequest = this.convert(source);
		FormDataResponse formDataResponse = formDataService.submitFormData(formName, formDataRequest, dynamicFiles);
		return ResponseEntity.ok(formDataResponse);
	}

	private FormDataRequest convert(String source) throws JsonProcessingException {
		return new ObjectMapper().readValue(source, FormDataRequest.class);
	}

}
