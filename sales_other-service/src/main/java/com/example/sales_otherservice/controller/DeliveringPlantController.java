package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.DeliveringPlantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DeliveringPlantController {
    private final DeliveringPlantService deliveringPlantService;

    @PostMapping("/saveDp")
    public ResponseEntity<Object> saveDp(@Valid @RequestBody DeliveringPlantRequest deliveringPlantRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDp").toUriString());
        DeliveringPlantResponse saveDp = deliveringPlantService.saveDp(deliveringPlantRequest);
        return ResponseEntity.created(uri).body(saveDp);
    }

    @GetMapping("/getAllDp")
    public ResponseEntity<Object> getAllDp(@RequestParam boolean plant) {
        List<?> allDp;
        if (plant) {
            allDp = deliveringPlantService.getAllDpPlant();
        } else {
            allDp = deliveringPlantService.getAllDp();
        }
        return ResponseEntity.ok(allDp);
    }


    @GetMapping("/getDpById/{id}")
    public ResponseEntity<Object> getDpById(@PathVariable Long id, @RequestParam boolean plant) throws ResourceNotFoundException {
        Object dpById;
        if (plant) {
            dpById = deliveringPlantService.getDpPlantById(id);
        } else {
            dpById = deliveringPlantService.getDpById(id);
        }
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllDpTrue")
    public ResponseEntity<Object> listDpStatusTrue() {
        List<DeliveringPlantResponse> plantResponseList = deliveringPlantService.findAllStatusTrue();
        return ResponseEntity.ok(plantResponseList);
    }

    @PutMapping("/updateDp/{id}")
    public ResponseEntity<Object> updateDp(@PathVariable Long id, @Valid @RequestBody DeliveringPlantRequest updateDeliveringPlantRequest) throws ResourceNotFoundException, ResourceFoundException {
        DeliveringPlantResponse updateDp = deliveringPlantService.updateDp(id, updateDeliveringPlantRequest);
        return ResponseEntity.ok(updateDp);
    }

    @DeleteMapping("/deleteDp/{id}")
    public ResponseEntity<Object> deleteDp(@PathVariable Long id) throws ResourceNotFoundException {
        deliveringPlantService.deleteDpId(id);
        return ResponseEntity.noContent().build();
    }
}
