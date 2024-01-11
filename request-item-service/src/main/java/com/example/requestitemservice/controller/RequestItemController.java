package com.example.requestitemservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.requestitemservice.dto.request.RequestItemRequest;
import com.example.requestitemservice.dto.response.RequestItemResponse;
import com.example.requestitemservice.exceptions.ResourceNotFoundException;
import com.example.requestitemservice.service.interfaces.RequestItemService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class RequestItemController {
	private final RequestItemService requestItemService;

	@PostMapping(value = "/saveRequest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Save Request with Image", description = "Save a dictionary entry along with an image.")
	public ResponseEntity<Object> saveDictionary(
			@Parameter(name = "item", required = true, schema = @Schema(implementation = RequestItemRequest.class), description = "source") @RequestPart String source,
			@RequestParam(value = "file", required = true) MultipartFile file) {
		try {
			RequestItemRequest item = this.convert(source);
			RequestItemResponse savedItem = requestItemService.save(item, file);
			return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@GetMapping("/getRequest/{id}")
	public ResponseEntity<Object> getRequestItem(@PathVariable("id") Long id,
			@RequestParam(required = false) @Pattern(regexp = "full") String show) {
		Object requestItem;
		if (show == null || !show.equalsIgnoreCase("full")) {
			requestItem = requestItemService.getRequestItem(id);
		} else {
			requestItem = requestItemService.getMaterialItem(id);
		}
		return new ResponseEntity<>(requestItem, HttpStatus.OK);

	}

	@GetMapping("/getAllRequests")
	public ResponseEntity<Object> getAll(@RequestParam(required = false) @Pattern(regexp = "full") String show) {
		try {
			List<?> requestItems;
			if (show == null || !show.equalsIgnoreCase("full")) {
				requestItems = requestItemService.getAllRequestItem();
			} else {
				requestItems = requestItemService.getAllMaterialItem();
			}
			return new ResponseEntity<>(requestItems, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	@PutMapping(value = "/updateRequest/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> update(@PathVariable("id") Long id,
			@Parameter(name = "updatedItem", required = true, schema = @Schema(implementation = RequestItemRequest.class), description = "source") @RequestPart String source,
			@RequestParam(value = "file", required = true) MultipartFile file)
			throws JsonMappingException, JsonProcessingException {
		RequestItemRequest updatedItem = this.convert(source);
		RequestItemResponse updatedRequestItem = requestItemService.update(id, updatedItem, file);
		return new ResponseEntity<>(updatedRequestItem, HttpStatus.OK);
	}

	@DeleteMapping("/deleteRequest/{id}")
	public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
		try {
			requestItemService.delete(id);
			return new ResponseEntity<>(HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@DeleteMapping("/deleteBatchRequest")
	public ResponseEntity<Object> deleteBatchRequest(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		requestItemService.deleteBatchRequest(ids);
		return ResponseEntity.ok("Successfully deleted !!!");
	}

	private RequestItemRequest convert(String source) throws JsonMappingException, JsonProcessingException {
		return new ObjectMapper().readValue(source, RequestItemRequest.class);
	}
}
