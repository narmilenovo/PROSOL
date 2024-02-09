package com.example.createtemplateservice.jpa.controller;

import java.net.URI;
import java.util.List;

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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.createtemplateservice.exceptions.ResourceNotFoundException;
import com.example.createtemplateservice.jpa.dto.request.DictionaryRequest;
import com.example.createtemplateservice.jpa.dto.response.DictionaryResponse;
import com.example.createtemplateservice.jpa.service.interfaces.DictionaryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DictionaryController {

	private final DictionaryService dictionaryService;

	@PostMapping(value = "/saveDictionaryImage", consumes = {
			MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Save Dictionary with Image", description = "Save a dictionary entry along with an image.")
	public ResponseEntity<Object> saveDictionary(
			@Parameter(name = "dictionaryRequest", required = true, schema = @Schema(implementation = DictionaryRequest.class), description = "source") @RequestPart String source,
			@RequestParam(value = "file", required = true) MultipartFile file) throws JsonProcessingException {
		DictionaryRequest dictionaryRequest = this.convert(source);
		URI uri = URI.create(
				ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDictionaryImage").toUriString());
		DictionaryResponse savedDictionary = dictionaryService.saveDictionary(dictionaryRequest, file);
		return ResponseEntity.created(uri).body(savedDictionary);
	}

	private DictionaryRequest convert(String source) throws JsonProcessingException {
		return new ObjectMapper().readValue(source, DictionaryRequest.class);
	}

	@GetMapping("/getDictionaryById/{id}")
	public ResponseEntity<Object> getDictionaryById(@PathVariable Long id,
			@Pattern(regexp = "uom") @RequestParam(required = false) String show) throws ResourceNotFoundException {
		Object dictionaryById;
		if (show == null) {
			dictionaryById = dictionaryService.getDictionaryById(id, show);
		} else if (show.equals("uom")) {
			dictionaryById = dictionaryService.getDictionaryNmUomById(id, show);
		} else {
			dictionaryById = dictionaryService.getDictionaryById(id, show);
		}
		return ResponseEntity.ok().body(dictionaryById);
	}

	@GetMapping("/getAllDictionary")
	public ResponseEntity<Object> getAllDictionary(@Pattern(regexp = "uom") @RequestParam(required = false) String show)
			throws ResourceNotFoundException {
		List<?> allDictionary;
		if (show == null) {
			allDictionary = dictionaryService.getAllDictionary(show);
		} else if (show.equals("uom")) {
			allDictionary = dictionaryService.getAllDictionaryNmUom(show);
		} else {
			allDictionary = dictionaryService.getAllDictionary(show);

		}
		return ResponseEntity.ok().body(allDictionary);
	}

	@GetMapping("/noun-suggestions")
	public ResponseEntity<Object> getNounSuggestions(@RequestParam String noun) {
		List<String> nouns = dictionaryService.getNounSuggestions(noun);
		return ResponseEntity.ok(nouns);
	}

	@GetMapping("/modifier-suggestions")
	public ResponseEntity<Object> getModifierSuggestions(@RequestParam String noun) {
		List<String> modifiers = dictionaryService.getModifiersByNoun(noun);
		return ResponseEntity.ok(modifiers);
	}

	@GetMapping("/findByNounAndModifier")
	public ResponseEntity<Object> getRecordByNounAndModifer(@RequestParam String noun, @RequestParam String modifier) {
		DictionaryResponse dictionaryResponse = dictionaryService.getRecordByNounAndModifer(noun, modifier);
		return ResponseEntity.ok(dictionaryResponse);
	}

	@PutMapping(value = "/updatedDictionary/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> updateDictionary(@PathVariable Long id,
			@Parameter(name = "updateDictionaryRequest", required = true, schema = @Schema(implementation = DictionaryRequest.class), description = "source") @RequestPart String source,
			@RequestParam(value = "file", required = true) MultipartFile file)
			throws ResourceNotFoundException, JsonProcessingException {
		DictionaryRequest updateDictionaryRequest = this.convert(source);
		DictionaryResponse updateDictionary = dictionaryService.updateDictionary(id, updateDictionaryRequest, file);
		return ResponseEntity.ok(updateDictionary);
	}

	@DeleteMapping("/deleteDictionary/{id}")
	public ResponseEntity<Object> deleteDictionary(@PathVariable Long id) throws ResourceNotFoundException {
		dictionaryService.deleteDictionaryId(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(value = "/deleteBatchDictionary")
	public ResponseEntity<Object> deleteBatchDictionary(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		dictionaryService.deleteBatchDictionary(ids);
		return ResponseEntity.noContent().build();
	}

}
