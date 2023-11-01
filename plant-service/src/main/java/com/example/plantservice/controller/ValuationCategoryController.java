package com.example.plantservice.controller;

import com.example.plantservice.dto.request.ValuationCategoryRequest;
import com.example.plantservice.dto.response.ValuationCategoryResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.ValuationCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ValuationCategoryController {

    private final ValuationCategoryService valuationCategoryService;


    @GetMapping("/getAllValuationCategory")
    public ResponseEntity<Object> getAllValuationCategory() {
        List<ValuationCategoryResponse> valuationCategory = valuationCategoryService.getAllValuationCategory();
        return ResponseEntity.ok(valuationCategory);
    }

    @PutMapping("/updateValuationCategory/{id}")
    public ResponseEntity<Object> updateValuationCategory(@PathVariable Long id, @RequestBody ValuationCategoryRequest valuationCategoryRequest) throws ResourceNotFoundException, AlreadyExistsException {
        ValuationCategoryResponse updateValuationCategory = valuationCategoryService.updateValuationCategory(id, valuationCategoryRequest);
        return ResponseEntity.ok().body(updateValuationCategory);
    }

    @DeleteMapping("/deleteValuationCategory/{id}")
    public ResponseEntity<String> deleteValuationCategory(@PathVariable Long id) throws ResourceNotFoundException {
        valuationCategoryService.deleteValuationCategory(id);
        return ResponseEntity.ok().body("ValuationCategory of '" + id + "' is deleted");
    }

    @PostMapping("/saveValuationCategory")
    public ResponseEntity<Object> saveValuationCategory(@Valid @RequestBody ValuationCategoryRequest valuationCategoryRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveValuationCategory").toUriString());
        ValuationCategoryResponse savedValuationCategory = valuationCategoryService.saveValuationCategory(valuationCategoryRequest);
        return ResponseEntity.created(uri).body(savedValuationCategory);
    }

    @GetMapping("/getValuationCategoryById/{id}")
    public ResponseEntity<Object> getValuationCategoryById(@PathVariable Long id) throws ResourceNotFoundException {
        ValuationCategoryResponse foundValuationCategory = valuationCategoryService.getValuationCategoryById(id);
        return ResponseEntity.ok(foundValuationCategory);
    }

    @PatchMapping("/updateValuationCategoryById/{id}")
    public ResponseEntity<Object> updateValuationCategoryStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        ValuationCategoryResponse response = valuationCategoryService.updateStatusUsingValuationCategoryId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusValuationCategoryId/{id}")
    public ResponseEntity<Object> updateBulkStatusValuationCategoryId(@PathVariable List<Long> id) {
        List<ValuationCategoryResponse> responseList = valuationCategoryService.updateBulkStatusValuationCategoryId(id);
        return ResponseEntity.ok(responseList);
    }
}
