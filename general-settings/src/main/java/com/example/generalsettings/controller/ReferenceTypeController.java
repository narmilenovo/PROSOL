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
import com.example.generalsettings.request.ReferenceTypeRequest;
import com.example.generalsettings.response.ReferenceTypeResponse;
import com.example.generalsettings.service.ReferenceTypeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReferenceTypeController {
	  private final ReferenceTypeService referenceTypeService;

	    @PostMapping("/saveReferenceType")
	    public ResponseEntity<Object> saveReferenceType(@Valid @RequestBody ReferenceTypeRequest referenceTypeRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveReferenceType").toUriString());
	        ReferenceTypeResponse savedReferenceType = referenceTypeService.saveReferenceType(referenceTypeRequest);
	        return ResponseEntity.created(uri).body(savedReferenceType);
	    }

	    @PutMapping("/updateReferenceType/{id}")
	    public ResponseEntity<Object> updateReferenceType(@PathVariable Long id, @RequestBody ReferenceTypeRequest referenceTypeRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        ReferenceTypeResponse updateReferenceType = referenceTypeService.updateReferenceType(id, referenceTypeRequest);
	        return ResponseEntity.ok().body(updateReferenceType);
	    }

	    @GetMapping("/getReferenceTypeById/{id}")
	    public ResponseEntity<Object> getReferenceTypeById(@PathVariable Long id) throws ResourceNotFoundException {
	        ReferenceTypeResponse foundReferenceType = referenceTypeService.getReferenceTypeById(id);
	        return ResponseEntity.ok(foundReferenceType);
	    }



	    
	    @DeleteMapping("/deleteReferenceType/{id}")
	    public ResponseEntity<String> deleteReferenceType(@PathVariable Long id) throws ResourceNotFoundException {
	        referenceTypeService.deleteReferenceType(id);
	        return ResponseEntity.ok().body("ReferenceType of '" + id + "' is deleted");
	    }

	    @PatchMapping("/updateReferenceTypeById/{id}")
	    public ResponseEntity<Object> updateReferenceTypeStatusId(@PathVariable Long id) throws ResourceNotFoundException {
	        ReferenceTypeResponse response = referenceTypeService.updateStatusUsingReferenceTypeId(id);
	        return ResponseEntity.ok(response);
	    }

	    @PatchMapping("/updateBulkStatusReferenceTypeId/{id}")
	    public ResponseEntity<Object> updateBulkStatusReferenceTypeId(@PathVariable List<Long> id) {
	        List<ReferenceTypeResponse> responseList = referenceTypeService.updateBulkStatusReferenceTypeId(id);
	        return ResponseEntity.ok(responseList);
	    }

	    @GetMapping("/getAllReferenceType")
	    public ResponseEntity<Object> getAllReferenceType() {
	        List<ReferenceTypeResponse> referenceType = referenceTypeService.getAllReferenceType();
	        return ResponseEntity.ok(referenceType);
	    }
	    
}
