package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.TaxClassificationTypeRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationTypeResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.TaxClassificationTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaxClassificationTypeController {
    private final TaxClassificationTypeService taxClassificationTypeService;

    @PostMapping("/saveTct")
    public ResponseEntity<Object> saveTct(@Valid @RequestBody TaxClassificationTypeRequest taxClassificationClassRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveTct").toUriString());
        TaxClassificationTypeResponse saveTct = taxClassificationTypeService.saveTct(taxClassificationClassRequest);
        return ResponseEntity.created(uri).body(saveTct);
    }

    @GetMapping("/getAllTct")
    public ResponseEntity<Object> getAllTct() {
        List<TaxClassificationTypeResponse> allTct = taxClassificationTypeService.getAllTct();
        return ResponseEntity.ok(allTct);
    }


    @GetMapping("/getTctById/{id}")
    public ResponseEntity<Object> getTctById(@PathVariable Long id) throws ResourceNotFoundException {
        TaxClassificationTypeResponse dpById = taxClassificationTypeService.getTctById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllTctTrue")
    public ResponseEntity<Object> listTctStatusTrue() {
        List<TaxClassificationTypeResponse> classResponseList = taxClassificationTypeService.findAllStatusTrue();
        return ResponseEntity.ok(classResponseList);
    }

    @PutMapping("/updateTct/{id}")
    public ResponseEntity<Object> updateTct(@PathVariable Long id, @Valid @RequestBody TaxClassificationTypeRequest updateTaxClassificationTypeRequest) throws ResourceNotFoundException, ResourceFoundException {
        TaxClassificationTypeResponse updateTct = taxClassificationTypeService.updateTct(id, updateTaxClassificationTypeRequest);
        return ResponseEntity.ok(updateTct);
    }

    @DeleteMapping("/deleteTct/{id}")
    public ResponseEntity<Object> deleteTct(@PathVariable Long id) throws ResourceNotFoundException {
        taxClassificationTypeService.deleteTctById(id);
        return ResponseEntity.noContent().build();
    }
}
