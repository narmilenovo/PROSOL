package com.example.generalservice.controller;

import com.example.generalservice.dto.request.MaterialTypeRequest;
import com.example.generalservice.dto.response.MaterialTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.MaterialTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class MaterialTypeController {
    private final MaterialTypeService materialTypeService;


    @PostMapping("/saveMaterial")
    public ResponseEntity<Object> saveMaterial(@Valid @RequestBody MaterialTypeRequest alternateUOMRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveMaterial").toUriString());
        MaterialTypeResponse savedMaterial = materialTypeService.saveMaterial(alternateUOMRequest);
        return ResponseEntity.created(uri).body(savedMaterial);
    }

    @GetMapping("/getAllMaterial")
    public ResponseEntity<Object> getAllMaterial() {
        List<MaterialTypeResponse> allMaterial = materialTypeService.getAllMaterial();
        return ResponseEntity.ok(allMaterial);
    }


    @GetMapping("/getMaterialById/{id}")
    public ResponseEntity<Object> getMaterialById(@PathVariable Long id) throws ResourceNotFoundException {
        MaterialTypeResponse materialTypeResponse = materialTypeService.getMaterialById(id);
        return ResponseEntity.ok(materialTypeResponse);
    }

    @GetMapping("/getAllMaterialTrue")
    public ResponseEntity<Object> listMaterialStatusTrue() {
        List<MaterialTypeResponse> materialTypeResponses = materialTypeService.findAllStatusTrue();
        return ResponseEntity.ok(materialTypeResponses);
    }

    @PutMapping("/updateMaterial/{id}")
    public ResponseEntity<Object> updateMaterial(@PathVariable Long id, @Valid @RequestBody MaterialTypeRequest updateMaterialTypeRequest) throws ResourceNotFoundException, ResourceFoundException {
        MaterialTypeResponse updatedMaterial = materialTypeService.updateMaterial(id, updateMaterialTypeRequest);
        return ResponseEntity.ok(updatedMaterial);
    }

    @DeleteMapping("/deleteMaterial/{id}")
    public ResponseEntity<Object> deleteMaterial(@PathVariable Long id) throws ResourceNotFoundException {
        materialTypeService.deleteMaterialId(id);
        return ResponseEntity.noContent().build();
    }
}
