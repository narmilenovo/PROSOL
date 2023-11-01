package com.example.generalsettings.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.SourceTypeRequest;
import com.example.generalsettings.response.SourceTypeResponse;
import com.example.generalsettings.service.SourceTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SourceTypeController {
	  private final SourceTypeService sourceTypeService;

	    @PostMapping("/saveSourceType")
	    public ResponseEntity<Object> saveSourceType(@Valid @RequestBody SourceTypeRequest sourceTypeRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSourceType").toUriString());
	        SourceTypeResponse savedSourceType = sourceTypeService.saveSourceType(sourceTypeRequest);
	        return ResponseEntity.created(uri).body(savedSourceType);
	    }

	    @PutMapping("/updateSourceType/{id}")
	    public ResponseEntity<Object> updateSourceType(@PathVariable Long id, @RequestBody SourceTypeRequest sourceTypeRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        SourceTypeResponse updateSourceType = sourceTypeService.updateSourceType(id, sourceTypeRequest);
	        return ResponseEntity.ok().body(updateSourceType);
	    }

	    @GetMapping("/getSourceTypeById/{id}")
	    public ResponseEntity<Object> getSourceTypeById(@PathVariable Long id) throws ResourceNotFoundException {
	        SourceTypeResponse foundSourceType = sourceTypeService.getSourceTypeById(id);
	        return ResponseEntity.ok(foundSourceType);
	    }

	    @DeleteMapping("/deleteSourceType/{id}")
	    public ResponseEntity<String> deleteSourceType(@PathVariable Long id) throws ResourceNotFoundException {
	        sourceTypeService.deleteSourceType(id);
	        return ResponseEntity.ok().body("SourceType of '" + id + "' is deleted");
	    }

	    @PatchMapping("/updateSourceTypeById/{id}")
	    public ResponseEntity<Object> updateSourceTypeStatusId(@PathVariable Long id) throws ResourceNotFoundException {
	        SourceTypeResponse response = sourceTypeService.updateStatusUsingSourceTypeId(id);
	        return ResponseEntity.ok(response);
	    }

	    @PatchMapping("/updateBulkStatusSourceTypeId/{id}")
	    public ResponseEntity<Object> updateBulkStatusSourceTypeId(@PathVariable List<Long> id) {
	        List<SourceTypeResponse> responseList = sourceTypeService.updateBulkStatusSourceTypeId(id);
	        return ResponseEntity.ok(responseList);
	    }

	    @GetMapping("/getAllSourceType")
	    public ResponseEntity<Object> getAllSourceType() {
	        List<SourceTypeResponse> sourceType = sourceTypeService.getAllSourceType();
	        return ResponseEntity.ok(sourceType);
	    }
}
