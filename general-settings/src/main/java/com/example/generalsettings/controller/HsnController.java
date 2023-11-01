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
import com.example.generalsettings.request.HsnRequest;
import com.example.generalsettings.response.HsnResponse;
import com.example.generalsettings.service.HsnService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class HsnController {
	private final HsnService hsnService;

    @PostMapping("/saveHsn")
    public ResponseEntity<Object> saveHsn(@Valid @RequestBody HsnRequest hsnRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveHsn").toUriString());
        HsnResponse savedHsn = hsnService.saveHsn(hsnRequest);
        return ResponseEntity.created(uri).body(savedHsn);
    }

    @PutMapping("/updateHsn/{id}")
    public ResponseEntity<Object> updateHsn(@PathVariable Long id, @RequestBody HsnRequest hsnRequest) throws ResourceNotFoundException, AlreadyExistsException {
        HsnResponse updateHsn = hsnService.updateHsn(id, hsnRequest);
        return ResponseEntity.ok().body(updateHsn);
    }

    @GetMapping("/getHsnById/{id}")
    public ResponseEntity<Object> getHsnById(@PathVariable Long id) throws ResourceNotFoundException {
        HsnResponse foundHsn = hsnService.getHsnById(id);
        return ResponseEntity.ok(foundHsn);
    }

    @DeleteMapping("/deleteHsn/{id}")
    public ResponseEntity<String> deleteHsn(@PathVariable Long id) throws ResourceNotFoundException {
        hsnService.deleteHsn(id);
        return ResponseEntity.ok().body("Hsn of '" + id + "' is deleted");
    }

    @PatchMapping("/updateHsnById/{id}")
    public ResponseEntity<Object> updateHsnStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        HsnResponse response = hsnService.updateStatusUsingHsnId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusHsnId/{id}")
    public ResponseEntity<Object> updateBulkStatusHsnId(@PathVariable List<Long> id) {
        List<HsnResponse> responseList = hsnService.updateBulkStatusHsnId(id);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/getAllHsn")
    public ResponseEntity<Object> getAllHsn() {
        List<HsnResponse> hsn = hsnService.getAllHsn();
        return ResponseEntity.ok(hsn);
    }
}
