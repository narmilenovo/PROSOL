package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.SalesOrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SalesOrganizationController {
    private final SalesOrganizationService salesOrganizationService;

    @PostMapping("/saveSo")
    public ResponseEntity<Object> saveSo(@Valid @RequestBody SalesOrganizationRequest salesOrganizationRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveSo").toUriString());
        SalesOrganizationResponse saveSo = salesOrganizationService.saveSo(salesOrganizationRequest);
        return ResponseEntity.created(uri).body(saveSo);
    }

    @GetMapping("/getAllSo")
    public ResponseEntity<Object> getAllSo() {
        List<SalesOrganizationResponse> allSo = salesOrganizationService.getAllSo();
        return ResponseEntity.ok(allSo);
    }


    @GetMapping("/getSoById/{id}")
    public ResponseEntity<Object> getSoById(@PathVariable Long id) throws ResourceNotFoundException {
        SalesOrganizationResponse dpById = salesOrganizationService.getSoById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllSoTrue")
    public ResponseEntity<Object> listSoStatusTrue() {
        List<SalesOrganizationResponse> valueKeyResponseList = salesOrganizationService.findAllStatusTrue();
        return ResponseEntity.ok(valueKeyResponseList);
    }

    @PutMapping("/updateSo/{id}")
    public ResponseEntity<Object> updateSo(@PathVariable Long id, @Valid @RequestBody SalesOrganizationRequest updateSalesOrganizationRequest) throws ResourceNotFoundException, ResourceFoundException {
        SalesOrganizationResponse updateSo = salesOrganizationService.updateSo(id, updateSalesOrganizationRequest);
        return ResponseEntity.ok(updateSo);
    }

    @DeleteMapping("/deleteSo/{id}")
    public ResponseEntity<Object> deleteSo(@PathVariable Long id) throws ResourceNotFoundException {
        salesOrganizationService.deleteSoById(id);
        return ResponseEntity.noContent().build();
    }
}