package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.TaxClassificationClassService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TaxClassificationClassController {
    private final TaxClassificationClassService taxClassificationClassService;

    @PostMapping("/saveTcc")
    public ResponseEntity<Object> saveTcc(@Valid @RequestBody TaxClassificationClassRequest taxClassificationClassRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveTcc").toUriString());
        TaxClassificationClassResponse saveTcc = taxClassificationClassService.saveTcc(taxClassificationClassRequest);
        return ResponseEntity.created(uri).body(saveTcc);
    }

    @GetMapping("/getAllTcc")
    public ResponseEntity<Object> getAllTcc() {
        List<TaxClassificationClassResponse> allTcc = taxClassificationClassService.getAllTcc();
        return ResponseEntity.ok(allTcc);
    }


    @GetMapping("/getTccById/{id}")
    public ResponseEntity<Object> getTccById(@PathVariable Long id) throws ResourceNotFoundException {
        TaxClassificationClassResponse dpById = taxClassificationClassService.getTccById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllTccTrue")
    public ResponseEntity<Object> listTccStatusTrue() {
        List<TaxClassificationClassResponse> classResponseList = taxClassificationClassService.findAllStatusTrue();
        return ResponseEntity.ok(classResponseList);
    }

    @PutMapping("/updateTcc/{id}")
    public ResponseEntity<Object> updateTcc(@PathVariable Long id, @Valid @RequestBody TaxClassificationClassRequest updateTaxClassificationClassRequest) throws ResourceNotFoundException, ResourceFoundException {
        TaxClassificationClassResponse updateTcc = taxClassificationClassService.updateTcc(id, updateTaxClassificationClassRequest);
        return ResponseEntity.ok(updateTcc);
    }

    @DeleteMapping("/deleteTcc/{id}")
    public ResponseEntity<Object> deleteTcc(@PathVariable Long id) throws ResourceNotFoundException {
        taxClassificationClassService.deleteTccById(id);
        return ResponseEntity.noContent().build();
    }
}
