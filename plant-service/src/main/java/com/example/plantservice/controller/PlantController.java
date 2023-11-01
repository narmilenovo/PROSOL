package com.example.plantservice.controller;

import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PlantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PlantController {

    private final PlantService plantService;

    @GetMapping("/getAllPlant")
    public ResponseEntity<Object> getAllPlants() {
        List<PlantResponse> plants = plantService.getAllPlants();
        return ResponseEntity.ok(plants);
    }

    @PutMapping("/updatePlant/{id}")
    public ResponseEntity<Object> updatePlant(@PathVariable Long id, @RequestBody PlantRequest plantRequest) throws ResourceNotFoundException, AlreadyExistsException {
        PlantResponse updatePlant = plantService.updatePlant(id, plantRequest);
        return ResponseEntity.ok().body(updatePlant);
    }

    @DeleteMapping("/deletePlant/{id}")
    public ResponseEntity<String> deletePlant(@PathVariable Long id) throws ResourceNotFoundException {
        plantService.deletePlant(id);
        return ResponseEntity.ok().body("Plant of '" + id + "' is deleted");
    }

    @PostMapping("/savePlant")
    public ResponseEntity<Object> savePlant(@Valid @RequestBody PlantRequest plantRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePlant").toUriString());
        PlantResponse savedPlant = plantService.savePlant(plantRequest);
        return ResponseEntity.created(uri).body(savedPlant);
    }

    @GetMapping("/getPlantById/{plantId}")
    public ResponseEntity<Object> getPlantById(@PathVariable Long plantId) throws ResourceNotFoundException {
        PlantResponse foundPlant = plantService.getPlantById(plantId);
        return ResponseEntity.ok(foundPlant);
    }

    @GetMapping("/getPlantByName/{name}")
    public ResponseEntity<Object> getPlantByName(@PathVariable String name) throws ResourceNotFoundException {
        PlantResponse foundPlant = plantService.getPlantByName(name);
        return ResponseEntity.ok(foundPlant);
    }

    @PatchMapping("/updatePlantStatusById/{id}")
    public ResponseEntity<Object> updatePlantStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        PlantResponse response = plantService.updateStatusUsingPlantId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusPlantId/{id}")
    public ResponseEntity<Object> updateBulkStatusPlantId(@PathVariable List<Long> id) {
        List<PlantResponse> responseList = plantService.updateBulkStatusPlantId(id);
        return ResponseEntity.ok(responseList);
    }



}
