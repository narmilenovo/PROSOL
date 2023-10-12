package com.example.generalservice.controller;

import com.example.generalservice.dto.request.IndustrySectorRequest;
import com.example.generalservice.dto.response.IndustrySectorResponse;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.IndustrySectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class IndustrySectorController {

    private final IndustrySectorService industrySectorService;


    @PostMapping("/saveSector")
    public ResponseEntity<Object> saveSector(@Valid @RequestBody IndustrySectorRequest industrySectorRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSector").toUriString());
        IndustrySectorResponse sectorResponse = industrySectorService.saveSector(industrySectorRequest);
        return ResponseEntity.created(uri).body(sectorResponse);
    }

    @GetMapping("/getAllSector")
    public ResponseEntity<Object> getAllSector() {
        List<IndustrySectorResponse> allSector = industrySectorService.getAllSector();
        return ResponseEntity.ok(allSector);
    }


    @GetMapping("/getSectorById/{id}")
    public ResponseEntity<Object> getSectorById(@PathVariable Long id) throws ResourceNotFoundException {
        IndustrySectorResponse sectorResponse = industrySectorService.getSectorById(id);
        return ResponseEntity.ok(sectorResponse);
    }

    @GetMapping("/getAllSectorTrue")
    public ResponseEntity<Object> listSectorStatusTrue() {
        List<IndustrySectorResponse> sectorResponses = industrySectorService.findAllStatusTrue();
        return ResponseEntity.ok(sectorResponses);
    }

    @PutMapping("/updateSector/{id}")
    public ResponseEntity<Object> updateSector(@PathVariable Long id, @Valid @RequestBody IndustrySectorRequest updateindustrysectorrequest) throws ResourceNotFoundException {
        IndustrySectorResponse updateSector = industrySectorService.updateSector(id, updateindustrysectorrequest);
        return ResponseEntity.ok(updateSector);
    }

    @DeleteMapping("/deleteSector/{id}")
    public ResponseEntity<Object> deleteSector(@PathVariable Long id) throws ResourceNotFoundException {
        industrySectorService.deleteSectorId(id);
        return ResponseEntity.noContent().build();
    }
}
