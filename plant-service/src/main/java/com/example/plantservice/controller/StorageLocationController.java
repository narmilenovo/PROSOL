package com.example.plantservice.controller;

import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PlantService;
import com.example.plantservice.service.interfaces.StorageLocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StorageLocationController {

    private final StorageLocationService storageLocationService;


    private final PlantService plantService;

    @GetMapping("/getAllStorageLocation")
    public ResponseEntity<Object> getAllStorageLocation() {
        List<StorageLocationResponse> storageLocation = storageLocationService.getAllStorageLocation();
        return ResponseEntity.ok(storageLocation);
    }

    @PutMapping("/updateStorageLocation/{id}")
    public ResponseEntity<Object> updateStorageLocation(@PathVariable Long id, @RequestBody StorageLocationRequest storageLocationRequest) throws ResourceNotFoundException, AlreadyExistsException {
        StorageLocationResponse updateStorageLocation = storageLocationService.updateStorageLocation(id, storageLocationRequest);
        return ResponseEntity.ok().body(updateStorageLocation);
    }

    @DeleteMapping("/deleteStorageLocation/{id}")
    public ResponseEntity<String> deleteStorageLocation(@PathVariable Long id) throws ResourceNotFoundException {
        storageLocationService.deleteStorageLocation(id);
        return ResponseEntity.ok().body("StorageLocation of '" + id + "' is deleted");
    }

    @PostMapping("/saveStorageLocation")
    public ResponseEntity<Object> saveStorageLocation(@Valid @RequestBody StorageLocationRequest storageLocationRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveStorageLocation").toUriString());
        StorageLocationResponse savedStorageLocation = storageLocationService.saveStorageLocation(storageLocationRequest);
        return ResponseEntity.created(uri).body(savedStorageLocation);
    }

    @GetMapping("/getStorageLocationById/{id}")
    public ResponseEntity<Object> getStorageLocationById(@PathVariable Long id) throws ResourceNotFoundException {
        StorageLocationResponse foundStorageLocation = storageLocationService.getStorageLocationById(id);
        return ResponseEntity.ok(foundStorageLocation);
    }

    @GetMapping("/getPlantAll3")
    public ResponseEntity<Object> getAllPlant() {
        List<PlantResponse> plants = plantService.getAllPlants();
        return ResponseEntity.ok(plants);
    }

    @PatchMapping("/updateStorageLocationStatusById1/{id}")
    public ResponseEntity<Object> updateStorageLocationStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        StorageLocationResponse response = storageLocationService.updateStatusUsingStorageLocationId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStorageLocationId1/{id}")
    public ResponseEntity<Object> updateBulkStatusStorageLocationId(@PathVariable List<Long> id) {
        List<StorageLocationResponse> responseList = storageLocationService.updateBulkStatusStorageLocationId(id);
        return ResponseEntity.ok(responseList);
    }
}
