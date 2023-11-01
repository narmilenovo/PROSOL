package com.example.plantservice.controller;

import com.example.plantservice.dto.request.StorageBinRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PlantService;
import com.example.plantservice.service.interfaces.StorageLocationBinService;
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
public class StorageLocationBinController {

    private final StorageLocationBinService storageLocationBinService;


    private final PlantService plantService;


    private final StorageLocationService storageLocationService;

    @GetMapping("/getAllStorageLocationBin")
    public ResponseEntity<Object> getAllStorageLocationBin() {
        List<StorageBinResponse> storageLocationBin = storageLocationBinService.getAllStorageLocationBin();
        return ResponseEntity.ok(storageLocationBin);
    }

    @PutMapping("/updateStorageLocationBin/{id}")
    public ResponseEntity<Object> updateStorageLocation(@PathVariable Long id, @RequestBody StorageBinRequest storageBinRequest) throws ResourceNotFoundException, AlreadyExistsException {
        StorageBinResponse updateStorageLocationBin = storageLocationBinService.updateStorageLocationBin(id, storageBinRequest);
        return ResponseEntity.ok().body(updateStorageLocationBin);
    }

    @DeleteMapping("/deleteStorageLocationBin/{id}")
    public ResponseEntity<String> deleteStorageLocationBin(@PathVariable Long id) throws ResourceNotFoundException {
        storageLocationBinService.deleteStorageLocationBin(id);
        return ResponseEntity.ok().body("StorageLocationBin of '" + id + "' is deleted");
    }

    @PostMapping("/saveStorageLocationBin")
    public ResponseEntity<Object> saveStorageLocationBin(@Valid @RequestBody StorageBinRequest storageBinRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveStorageLocationBin").toUriString());
        StorageBinResponse savedStorageLocationBin = storageLocationBinService.saveStorageLocation(storageBinRequest);
        return ResponseEntity.created(uri).body(savedStorageLocationBin);
    }

    @GetMapping("/getStorageLocationBinById/{id}")
    public ResponseEntity<Object> getStorageLocationBinById(@PathVariable Long id) throws ResourceNotFoundException {
        StorageBinResponse foundStorageLocationBin = storageLocationBinService.getStorageLocationBinById(id);
        return ResponseEntity.ok(foundStorageLocationBin);
    }

    @GetMapping("/getPlantAll2")
    public ResponseEntity<Object> getAllStorageLocation() {
        List<PlantResponse> plants = plantService.getAllPlants();
        return ResponseEntity.ok(plants);
    }

    @GetMapping("/getAllStorageLocation1")
    public ResponseEntity<Object> getAllPlant() {
        List<StorageLocationResponse> storageLocation = storageLocationService.getAllStorageLocation();
        return ResponseEntity.ok(storageLocation);
    }


    @PatchMapping("/updateStorageLocationStatusById/{id}")
    public ResponseEntity<Object> updateStorageLocationBinStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        StorageBinResponse response = storageLocationBinService.updateStatusUsingStorageLocationBinId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStorageLocationId/{id}")
    public ResponseEntity<Object> updateBulkStatusStorageLocationBinId(@PathVariable List<Long> id) {
        List<StorageBinResponse> responseList = storageLocationBinService.updateBulkStatusStorageLocationBinId(id);
        return ResponseEntity.ok(responseList);
    }
}
