package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.PurchasingValueKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PurchasingValueKeyController {
    private final PurchasingValueKeyService purchasingValueKeyService;

    @PostMapping("/savePvk")
    public ResponseEntity<Object> savePvk(@Valid @RequestBody PurchasingValueKeyRequest purchasingValueKeyRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePvk").toUriString());
        PurchasingValueKeyResponse savePvk = purchasingValueKeyService.savePvk(purchasingValueKeyRequest);
        return ResponseEntity.created(uri).body(savePvk);
    }

    @GetMapping("/getAllPvk")
    public ResponseEntity<Object> getAllPvk() {
        List<PurchasingValueKeyResponse> allPvk = purchasingValueKeyService.getAllPvk();
        return ResponseEntity.ok(allPvk);
    }


    @GetMapping("/getPvkById/{id}")
    public ResponseEntity<Object> getPvkById(@PathVariable Long id) throws ResourceNotFoundException {
        PurchasingValueKeyResponse dpById = purchasingValueKeyService.getPvkById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllPvkTrue")
    public ResponseEntity<Object> listPvkStatusTrue() {
        List<PurchasingValueKeyResponse> valueKeyResponseList = purchasingValueKeyService.findAllStatusTrue();
        return ResponseEntity.ok(valueKeyResponseList);
    }

    @PutMapping("/updatePvk/{id}")
    public ResponseEntity<Object> updatePvk(@PathVariable Long id, @Valid @RequestBody PurchasingValueKeyRequest updatePurchasingValueKeyRequest) throws ResourceNotFoundException, ResourceFoundException {
        PurchasingValueKeyResponse updatePvk = purchasingValueKeyService.updatePvk(id, updatePurchasingValueKeyRequest);
        return ResponseEntity.ok(updatePvk);
    }

    @DeleteMapping("/deletePvk/{id}")
    public ResponseEntity<Object> deletePvk(@PathVariable Long id) throws ResourceNotFoundException {
        purchasingValueKeyService.deletePvkById(id);
        return ResponseEntity.noContent().build();
    }
}
