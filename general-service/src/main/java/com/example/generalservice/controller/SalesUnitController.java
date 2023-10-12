package com.example.generalservice.controller;

import com.example.generalservice.dto.request.SalesUnitRequest;
import com.example.generalservice.dto.response.SalesUnitResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.SalesUnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SalesUnitController {
    private final SalesUnitService salesUnitService;

    @PostMapping("/saveSalesUnit")
    public ResponseEntity<Object> saveSalesUnit(@Valid @RequestBody SalesUnitRequest salesUnitRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveUom").toUriString());
        SalesUnitResponse saveSalesUnit = salesUnitService.saveSalesUnit(salesUnitRequest);
        return ResponseEntity.created(uri).body(saveSalesUnit);
    }

    @GetMapping("/getAllSalesUnit")
    public ResponseEntity<Object> getAllSalesUnit() {
        List<SalesUnitResponse> salesUnit = salesUnitService.getAllSalesUnit();
        return ResponseEntity.ok(salesUnit);
    }


    @GetMapping("/getSalesUnitById/{id}")
    public ResponseEntity<Object> getSalesUnitById(@PathVariable Long id) throws ResourceNotFoundException {
        SalesUnitResponse salesUnitResponse = salesUnitService.getSalesUnitById(id);
        return ResponseEntity.ok(salesUnitResponse);
    }

    @GetMapping("/getAllSalesUnitTrue")
    public ResponseEntity<Object> listSalesUnitStatusTrue() {
        List<SalesUnitResponse> salesUnitResponses = salesUnitService.findAllStatusTrue();
        return ResponseEntity.ok(salesUnitResponses);
    }

    @PutMapping("/updateSalesUnit/{id}")
    public ResponseEntity<Object> updateSalesUnit(@PathVariable Long id, @Valid @RequestBody SalesUnitRequest updateSalesUnitRequest) throws ResourceNotFoundException, ResourceFoundException {
        SalesUnitResponse updateSalesUnit = salesUnitService.updateSalesUnit(id, updateSalesUnitRequest);
        return ResponseEntity.ok(updateSalesUnit);
    }

    @DeleteMapping("/deleteSalesUnit/{id}")
    public ResponseEntity<Object> deleteSalesUnit(@PathVariable Long id) throws ResourceNotFoundException {
        salesUnitService.deleteSalesUnitId(id);
        return ResponseEntity.noContent().build();
    }
}
