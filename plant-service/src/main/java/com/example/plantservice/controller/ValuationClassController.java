package com.example.plantservice.controller;

import com.example.plantservice.dto.request.ValuationClassRequest;
import com.example.plantservice.dto.response.ValuationClassResponse;
import com.example.plantservice.dto.response.ValuationMaterialResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.ValuationClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ValuationClassController {

    private final ValuationClassService valuationClassService;

    @PostMapping("/saveValuationClass")
    public ResponseEntity<Object> saveValuationClass(@Valid @RequestBody ValuationClassRequest valuationClassRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveValuationClass").toUriString());
        ValuationClassResponse savedValuationClass = valuationClassService.saveValuationClass(valuationClassRequest);
        return ResponseEntity.created(uri).body(savedValuationClass);
    }

    @PutMapping("/updateValuationClass/{id}")
    public ResponseEntity<Object> updateValuationClass(@PathVariable Long id, @RequestBody ValuationClassRequest valuationClassRequest) throws ResourceNotFoundException, AlreadyExistsException {
        ValuationClassResponse updateValuationClass = valuationClassService.updateValuationClass(id, valuationClassRequest);
        return ResponseEntity.ok().body(updateValuationClass);
    }

    @GetMapping("/getValuationClassById/{id}")
    public ResponseEntity<Object> getValuationClassById(@PathVariable Long id) throws ResourceNotFoundException {
        ValuationClassResponse foundValuationClass = valuationClassService.getValuationClassById(id);
        return ResponseEntity.ok(foundValuationClass);
    }

    @DeleteMapping("/deleteValuationClass/{id}")
    public ResponseEntity<String> deleteValuationClass(@PathVariable Long id) throws ResourceNotFoundException {
        valuationClassService.deleteValuationClass(id);
        return ResponseEntity.ok().body("ValuationClass of '" + id + "' is deleted");
    }

    @PatchMapping("/updateValuationClassById/{id}")
    public ResponseEntity<Object> updateValuationClassStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        ValuationClassResponse response = valuationClassService.updateStatusUsingValuationClassId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusValuationClassId/{id}")
    public ResponseEntity<Object> updateBulkStatusValuationClassId(@PathVariable List<Long> id) {
        List<ValuationClassResponse> responseList = valuationClassService.updateBulkStatusValuationClassId(id);
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/getAllValuationClass")
    public ResponseEntity<Object> getAllValuationClass() {
        List<ValuationClassResponse> valuationClass = valuationClassService.getAllValuationClass();
        return ResponseEntity.ok(valuationClass);
    }

    @GetMapping("/getAllValuationClassByMaterial")
    public ResponseEntity<Object> getAllValuationClassByMaterial() throws ResourceNotFoundException {
        List<ValuationMaterialResponse> materialResponse = valuationClassService.getAllValuationClassByMaterial();
        return ResponseEntity.ok(materialResponse);
    }
}
