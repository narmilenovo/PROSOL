package com.example.plantservice.controller;

import com.example.plantservice.dto.request.ProfitCenterRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.dto.response.ProfitCenterResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PlantService;
import com.example.plantservice.service.interfaces.ProfitCenterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfitCenterController {


    private final ProfitCenterService profitCenterService;


    private final PlantService plantService;

    @GetMapping("/getAllProfitCenter")
    public ResponseEntity<Object> getAllProfitCenter() {
        List<ProfitCenterResponse> profitCenters = profitCenterService.getAllProfitCenter();
        return ResponseEntity.ok(profitCenters);
    }

    @PutMapping("/updateProfitCenter/{id}")
    public ResponseEntity<Object> updateProfitCenter(@PathVariable Long id, @RequestBody ProfitCenterRequest profitCenterRequest) throws ResourceNotFoundException, AlreadyExistsException {
        ProfitCenterResponse updateprofitCenter = profitCenterService.updateProfitCenter(id, profitCenterRequest);
        return ResponseEntity.ok().body(updateprofitCenter);
    }

    @DeleteMapping("/deleteProfitCenter/{id}")
    public ResponseEntity<String> deleteProfitCenter(@PathVariable Long id) throws ResourceNotFoundException {
        profitCenterService.deleteProfitCenter(id);
        return ResponseEntity.ok().body("ProfitCenter of '" + id + "' is deleted");
    }

    @PostMapping("/saveProfitCenter")
    public ResponseEntity<Object> saveProfitCenter(@Valid @RequestBody ProfitCenterRequest profitCenterRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveProfitCenter").toUriString());
        ProfitCenterResponse savedProfitCenter = profitCenterService.saveProfitCenter(profitCenterRequest);
        return ResponseEntity.created(uri).body(savedProfitCenter);
    }

    @GetMapping("/getProfitCenterById/{id}")
    public ResponseEntity<Object> getProfitCenterById(@PathVariable Long id) throws ResourceNotFoundException {
        ProfitCenterResponse foundProfitCenter = profitCenterService.getProfitCenterById(id);
        return ResponseEntity.ok(foundProfitCenter);
    }

    @GetMapping("/getPlantAll1")
    public ResponseEntity<Object> getAllPlant() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/getAllPlant").toUriString());
        List<PlantResponse> plants = plantService.getAllPlants();
        return ResponseEntity.created(uri).body(plants);
    }

    @PatchMapping("/updateProfitCenterStatusById/{id}")
    public ResponseEntity<Object> updateProfitCenterStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        ProfitCenterResponse response = profitCenterService.updateStatusUsingProfitCenterId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusProfitCentertId/{id}")
    public ResponseEntity<Object> updateBulkStatusProfitCenterId(@PathVariable List<Long> id) {
        List<ProfitCenterResponse> responseList = profitCenterService.updateBulkStatusProfitCenterId(id);
        return ResponseEntity.ok(responseList);
    }
}
