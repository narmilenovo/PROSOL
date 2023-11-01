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
import com.example.generalsettings.request.AttributeUomRequest;
import com.example.generalsettings.response.AttributeUomResponse;
import com.example.generalsettings.service.AttributeUomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AttributeUomController {
	 private final AttributeUomService attributeUomService;

	    @PostMapping("/saveAttributeUom")
	    public ResponseEntity<Object> saveAttributeUom(@Valid @RequestBody AttributeUomRequest attributeUomRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveAttributeUom").toUriString());
	        AttributeUomResponse savedAttributeUom = attributeUomService.saveAttributeUom(attributeUomRequest);
	        return ResponseEntity.created(uri).body(savedAttributeUom);
	    }

	    @PutMapping("/updateAttributeUom/{id}")
	    public ResponseEntity<Object> updateAttributeUom(@PathVariable Long id, @RequestBody AttributeUomRequest attributeUomRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        AttributeUomResponse updateAttributeUom = attributeUomService.updateAttributeUom(id, attributeUomRequest);
	        return ResponseEntity.ok().body(updateAttributeUom);
	    }

	    @GetMapping("/getAttributeUomById/{id}")
	    public ResponseEntity<Object> getAttributeUomById(@PathVariable Long id) throws ResourceNotFoundException {
	        AttributeUomResponse foundAttributeUom = attributeUomService.getAttributeUomById(id);
	        return ResponseEntity.ok(foundAttributeUom);
	    }

	    @DeleteMapping("/deleteAttributeUom/{id}")
	    public ResponseEntity<String> deleteAttributeUom(@PathVariable Long id) throws ResourceNotFoundException {
	        attributeUomService.deleteAttributeUom(id);
	        return ResponseEntity.ok().body("AttributeUom of '" + id + "' is deleted");
	    }

	    @PatchMapping("/updateAttributeUomById/{id}")
	    public ResponseEntity<Object> updateAttributeUomStatusId(@PathVariable Long id) throws ResourceNotFoundException {
	        AttributeUomResponse response = attributeUomService.updateStatusUsingAttributeUomId(id);
	        return ResponseEntity.ok(response);
	    }

	    @PatchMapping("/updateBulkStatusAttributeUomId/{id}")
	    public ResponseEntity<Object> updateBulkStatusAttributeUomId(@PathVariable List<Long> id) {
	        List<AttributeUomResponse> responseList = attributeUomService.updateBulkStatusAttributeUomId(id);
	        return ResponseEntity.ok(responseList);
	    }

	    @GetMapping("/getAllAttributeUom")
	    public ResponseEntity<Object> getAllAttributeUom() {
	        List<AttributeUomResponse> attributeUom = attributeUomService.getAllAttributeUom();
	        return ResponseEntity.ok(attributeUom);
	    }
	    @GetMapping("/getAllAttributeUom/{uomId}")
	    public ResponseEntity<Object> getUomById(@PathVariable Long uomId) throws ResourceNotFoundException {
	    	AttributeUomResponse found = attributeUomService.getAttributeUomById(uomId);
	        return ResponseEntity.ok(found);
	    }
	    

}
