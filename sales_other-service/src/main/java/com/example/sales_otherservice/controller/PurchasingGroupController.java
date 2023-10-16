package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.PurchasingGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PurchasingGroupController {
    private final PurchasingGroupService purchasingGroupService;

    @PostMapping("/savePg")
    public ResponseEntity<Object> savePg(@Valid @RequestBody PurchasingGroupRequest purchasingGroupRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePg").toUriString());
        PurchasingGroupResponse savePg = purchasingGroupService.savePg(purchasingGroupRequest);
        return ResponseEntity.created(uri).body(savePg);
    }

    @GetMapping("/getAllPg")
    public ResponseEntity<Object> getAllPg() {
        List<PurchasingGroupResponse> allPg = purchasingGroupService.getAllPg();
        return ResponseEntity.ok(allPg);
    }


    @GetMapping("/getPgById/{id}")
    public ResponseEntity<Object> getPgById(@PathVariable Long id) throws ResourceNotFoundException {
        PurchasingGroupResponse dpById = purchasingGroupService.getPgById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllPgTrue")
    public ResponseEntity<Object> listPgStatusTrue() {
        List<PurchasingGroupResponse> groupResponseList = purchasingGroupService.findAllStatusTrue();
        return ResponseEntity.ok(groupResponseList);
    }

    @PutMapping("/updatePg/{id}")
    public ResponseEntity<Object> updatePg(@PathVariable Long id, @Valid @RequestBody PurchasingGroupRequest updatePurchasingGroupRequest) throws ResourceNotFoundException, ResourceFoundException {
        PurchasingGroupResponse updatePg = purchasingGroupService.updatePg(id, updatePurchasingGroupRequest);
        return ResponseEntity.ok(updatePg);
    }

    @DeleteMapping("/deletePg/{id}")
    public ResponseEntity<Object> deletePg(@PathVariable Long id) throws ResourceNotFoundException {
        purchasingGroupService.deletePgById(id);
        return ResponseEntity.noContent().build();
    }
}
