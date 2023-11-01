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
import com.example.generalsettings.request.NmUomRequest;
import com.example.generalsettings.response.NmUomResponse;
import com.example.generalsettings.service.NmUomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NmUomController {
	 private final NmUomService nmUomService;

	    @PostMapping("/saveNmUom")
	    public ResponseEntity<Object> saveNmUom(@Valid @RequestBody NmUomRequest nmUomRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveNmUom").toUriString());
	        NmUomResponse savedNmUom = nmUomService.saveNmUom(nmUomRequest);
	        return ResponseEntity.created(uri).body(savedNmUom);
	    }

	    @PutMapping("/updateNmUom/{id}")
	    public ResponseEntity<Object> updateNmUom(@PathVariable Long id, @RequestBody NmUomRequest nmUomRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        NmUomResponse updateNmUom = nmUomService.updateNmUom(id, nmUomRequest);
	        return ResponseEntity.ok().body(updateNmUom);
	    }

	    @GetMapping("/getNmUomById/{id}")
	    public ResponseEntity<Object> getNmUomById(@PathVariable Long id) throws ResourceNotFoundException {
	        NmUomResponse foundNmUom = nmUomService.getNmUomById(id);
	        return ResponseEntity.ok(foundNmUom);
	    }

	    @DeleteMapping("/deleteNmUom/{id}")
	    public ResponseEntity<String> deleteNmUom(@PathVariable Long id) throws ResourceNotFoundException {
	        nmUomService.deleteNmUom(id);
	        return ResponseEntity.ok().body("NmUom of '" + id + "' is deleted");
	    }

	    @PatchMapping("/updateNmUomById/{id}")
	    public ResponseEntity<Object> updateNmUomStatusId(@PathVariable Long id) throws ResourceNotFoundException {
	        NmUomResponse response = nmUomService.updateStatusUsingNmUomId(id);
	        return ResponseEntity.ok(response);
	    }

	    @PatchMapping("/updateBulkStatusNmUomId/{id}")
	    public ResponseEntity<Object> updateBulkStatusNmUomId(@PathVariable List<Long> id) {
	        List<NmUomResponse> responseList = nmUomService.updateBulkStatusNmUomId(id);
	        return ResponseEntity.ok(responseList);
	    }

	    @GetMapping("/getAllNmUom")
	    public ResponseEntity<Object> getAllNmUom() {
	        List<NmUomResponse> nmUom = nmUomService.getAllNmUom();
	        return ResponseEntity.ok(nmUom);
	    }
}
