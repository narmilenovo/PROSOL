package com.example.plantservice.controller;

import com.example.plantservice.dto.request.PriceControlRequest;
import com.example.plantservice.dto.response.PriceControlResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.PriceControlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PriceControlController {

    private final PriceControlService priceControlService;


    @GetMapping("/getAllPriceControl")
    public ResponseEntity<Object> getAllPriceControl() {
        List<PriceControlResponse> priceControls = priceControlService.getAllPriceControl();
        return ResponseEntity.ok(priceControls);
    }

    @PutMapping("/updatePriceControl/{id}")
    public ResponseEntity<Object> updatePriceControl(@PathVariable Long id, @RequestBody PriceControlRequest priceControlRequest) throws ResourceNotFoundException, AlreadyExistsException {
        PriceControlResponse updatePriceControl = priceControlService.updatePriceControl(id, priceControlRequest);
        return ResponseEntity.ok().body(updatePriceControl);
    }

    @DeleteMapping("/deletePriceControl/{id}")
    public ResponseEntity<String> deletePriceControl(@PathVariable Long id) throws ResourceNotFoundException {
        priceControlService.deletePriceControl(id);
        return ResponseEntity.ok().body("PriceControl of '" + id + "' is deleted");
    }

    @PostMapping("/savePriceControl")
    public ResponseEntity<Object> savePriceControl(@Valid @RequestBody PriceControlRequest priceControlRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePriceControl").toUriString());
        PriceControlResponse savedPriceControl = priceControlService.savePriceControl(priceControlRequest);
        return ResponseEntity.created(uri).body(savedPriceControl);
    }

    @GetMapping("/getPriceControlById/{id}")
    public ResponseEntity<Object> getPriceControlById(@PathVariable Long id) throws ResourceNotFoundException {
        PriceControlResponse foundPriceControl = priceControlService.getPriceControlById(id);
        return ResponseEntity.ok(foundPriceControl);
    }

    @PatchMapping("/updatePriceControlById/{id}")
    public ResponseEntity<Object> updatePriceControlStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        PriceControlResponse response = priceControlService.updateStatusUsingPriceControlId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusPriceControlId/{id}")
    public ResponseEntity<Object> updateBulkStatusPriceControlId(@PathVariable List<Long> id) {
        List<PriceControlResponse> responseList = priceControlService.updateBulkStatusPriceControlId(id);
        return ResponseEntity.ok(responseList);
    }
}
