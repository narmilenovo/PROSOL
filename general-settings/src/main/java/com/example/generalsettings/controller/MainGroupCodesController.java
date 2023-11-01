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
import jakarta.validation.Valid;
import com.example.generalsettings.config.AlreadyExistsException;
import com.example.generalsettings.config.ResourceNotFoundException;
import com.example.generalsettings.request.MainGroupCodesRequest;
import com.example.generalsettings.response.MainGroupCodesResponse;
import com.example.generalsettings.service.MainGroupCodesService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MainGroupCodesController {
	  private final MainGroupCodesService mainGroupCodesService;

	    @PostMapping("/saveMainGroupCodes")
	    public ResponseEntity<Object> saveMainGroupCodes(@Valid @RequestBody MainGroupCodesRequest mainGroupCodesRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveMainGroupCodes").toUriString());
	        MainGroupCodesResponse savedMainGroupCodes = mainGroupCodesService.saveMainGroupCodes(mainGroupCodesRequest);
	        return ResponseEntity.created(uri).body(savedMainGroupCodes);
	    }

	    @PutMapping("/updateMainGroupCodes/{id}")
	    public ResponseEntity<Object> updateMainGroupCodes(@PathVariable Long id, @RequestBody MainGroupCodesRequest mainGroupCodesRequest) throws ResourceNotFoundException, AlreadyExistsException {
	        MainGroupCodesResponse updateMainGroupCodes = mainGroupCodesService.updateMainGroupCodes(id, mainGroupCodesRequest);
	        return ResponseEntity.ok().body(updateMainGroupCodes);
	    }

	    @GetMapping("/getMainGroupCodesById/{id}")
	    public ResponseEntity<Object> getMainGroupCodesById(@PathVariable Long id) throws ResourceNotFoundException {
	        MainGroupCodesResponse foundMainGroupCodes = mainGroupCodesService.getMainGroupCodesById(id);
	        return ResponseEntity.ok(foundMainGroupCodes);
	    }

	    @DeleteMapping("/deleteMainGroupCodes/{id}")
	    public ResponseEntity<String> deleteMainGroupCodes(@PathVariable Long id) throws ResourceNotFoundException {
	        mainGroupCodesService.deleteMainGroupCodes(id);
	        return ResponseEntity.ok().body("MainGroupCodes of '" + id + "' is deleted");
	    }

	    @PatchMapping("/updateMainGroupCodesById/{id}")
	    public ResponseEntity<Object> updateMainGroupCodesStatusId(@PathVariable Long id) throws ResourceNotFoundException {
	        MainGroupCodesResponse response = mainGroupCodesService.updateStatusUsingMainGroupCodesId(id);
	        return ResponseEntity.ok(response);
	    }

	    @PatchMapping("/updateBulkStatusMainGroupCodesId/{id}")
	    public ResponseEntity<Object> updateBulkStatusMainGroupCodesId(@PathVariable List<Long> id) {
	        List<MainGroupCodesResponse> responseList = mainGroupCodesService.updateBulkStatusMainGroupCodesId(id);
	        return ResponseEntity.ok(responseList);
	    }

	    @GetMapping("/getAllMainGroupCodes")
	    public ResponseEntity<Object> getAllMainGroupCodes() {
	        List<MainGroupCodesResponse> mainGroupCodes = mainGroupCodesService.getAllMainGroupCodes();
	        return ResponseEntity.ok(mainGroupCodes);
	    }
}
