package com.example.generalservice.controller;

import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.AlternateUOMService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AlternateUOMController {

    private final AlternateUOMService alternateUOMService;


    @PostMapping("/saveUom")
    public ResponseEntity<Object> saveUom(@Valid @RequestBody AlternateUOMRequest alternateUOMRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveUom").toUriString());
        AlternateUOMResponse alternateUOMResponse = alternateUOMService.saveUom(alternateUOMRequest);
        return ResponseEntity.created(uri).body(alternateUOMResponse);
    }

    @GetMapping("/getAllUom")
    public ResponseEntity<Object> getAllUom(HttpServletRequest request) {
        List<AlternateUOMResponse> allUom = alternateUOMService.getAllUom();
        return ResponseEntity.ok(allUom);
    }


    @GetMapping("/getUomById/{id}")
    public ResponseEntity<Object> getUomById(@PathVariable Long id) throws ResourceNotFoundException {
        AlternateUOMResponse uomResponse = alternateUOMService.getUomById(id);
        return ResponseEntity.ok(uomResponse);
    }

    @GetMapping("/getAllUomTrue")
    public ResponseEntity<Object> listUomStatusTrue() {
        List<AlternateUOMResponse> uomResponses = alternateUOMService.findAllStatusTrue();
        return ResponseEntity.ok(uomResponses);
    }

    @PutMapping("/updateUom/{id}")
    public ResponseEntity<Object> updateUom(@PathVariable Long id, @Valid @RequestBody AlternateUOMRequest updateAlternateUOMRequest) throws ResourceNotFoundException {
        AlternateUOMResponse uomResponse = alternateUOMService.updateUom(id, updateAlternateUOMRequest);
        return ResponseEntity.ok(uomResponse);
    }

    @DeleteMapping("/deleteUom/{id}")
    public ResponseEntity<Object> deleteUom(@PathVariable Long id) throws ResourceNotFoundException {
        alternateUOMService.deleteUomId(id);
        return ResponseEntity.noContent().build();
    }


}
