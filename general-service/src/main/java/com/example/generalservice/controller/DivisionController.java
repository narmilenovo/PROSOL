package com.example.generalservice.controller;

import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.DivisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DivisionController {

    private final DivisionService divisionService;

    @PostMapping("/saveDivision")
    public ResponseEntity<Object> saveDivision(@Valid @RequestBody DivisionRequest divisionRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDivision").toUriString());
        DivisionResponse divisionResponse = divisionService.saveDivision(divisionRequest);
        return ResponseEntity.created(uri).body(divisionResponse);
    }

    @GetMapping("/getAllDivision")
    public ResponseEntity<Object> getAllDivision() {
        List<DivisionResponse> divisionResponses = divisionService.getAllDivision();
        return ResponseEntity.ok(divisionResponses);
    }


    @GetMapping("/getDivisionById/{id}")
    public ResponseEntity<Object> getDivisionById(@PathVariable Long id) throws ResourceNotFoundException {
        DivisionResponse divisionResponse = divisionService.getDivisionById(id);
        return ResponseEntity.ok(divisionResponse);
    }

    @GetMapping("/getAllDivisionTrue")
    public ResponseEntity<Object> listDivisionStatusTrue() {
        List<DivisionResponse> divisionResponses = divisionService.findAllStatusTrue();
        return ResponseEntity.ok(divisionResponses);
    }

    @PutMapping("/updateDivision/{id}")
    public ResponseEntity<Object> updateDivision(@PathVariable Long id, @Valid @RequestBody DivisionRequest updateDivisionRequest) throws ResourceNotFoundException {
        DivisionResponse divisionResponse = divisionService.updateDivision(id, updateDivisionRequest);
        return ResponseEntity.ok(divisionResponse);
    }

    @DeleteMapping("/deleteDivision/{id}")
    public ResponseEntity<Object> deleteDivision(@PathVariable Long id) throws ResourceNotFoundException {
        divisionService.deleteDivisionId(id);
        return ResponseEntity.noContent().build();
    }


}
