package com.example.vendor_masterservice.controller;

import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.exceptions.ResourceFoundException;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;
import com.example.vendor_masterservice.service.interfaces.VendorMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class VendorMasterController {

    private final VendorMasterService vendorMasterService;

    @PostMapping("/saveVm")
    public ResponseEntity<Object> saveVm(@Valid @RequestBody VendorMasterRequest vendorMasterRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveVm").toUriString());
        VendorMasterResponse savedVm = vendorMasterService.saveVm(vendorMasterRequest);
        return ResponseEntity.created(uri).body(savedVm);
    }

    @PostMapping("/saveAllVm")
    public ResponseEntity<Object> saveAllVm(@Valid @RequestBody List<VendorMasterRequest> vendorMasterRequests) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveAllVm").toUriString());
        List<VendorMasterResponse> savedAllVm = vendorMasterService.saveAllVm(vendorMasterRequests);
        return ResponseEntity.created(uri).body(savedAllVm);
    }

    @GetMapping("/getAllVm")
    public ResponseEntity<Object> getAllVm() {
        List<VendorMasterResponse> allVm = vendorMasterService.getAllVm();
        return ResponseEntity.ok(allVm);
    }


    @GetMapping("/getVmById/{id}")
    public ResponseEntity<Object> getVmById(@PathVariable Long id) throws ResourceNotFoundException {
        VendorMasterResponse vmById = vendorMasterService.getVmById(id);
        return ResponseEntity.ok(vmById);
    }

    @GetMapping("/getAllVmTrue")
    public ResponseEntity<Object> listVmStatusTrue() {
        List<VendorMasterResponse> masterResponses = vendorMasterService.findAllStatusTrue();
        return ResponseEntity.ok(masterResponses);
    }

    @PutMapping("/updateVm/{id}")
    public ResponseEntity<Object> updateVm(@PathVariable Long id, @Valid @RequestBody VendorMasterRequest updateVendorMasterRequest) throws ResourceNotFoundException {
        VendorMasterResponse masterResponse = vendorMasterService.updateVm(id, updateVendorMasterRequest);
        return ResponseEntity.ok(masterResponse);
    }

    @DeleteMapping("/deleteVm/{id}")
    public ResponseEntity<Object> deleteVm(@PathVariable Long id) throws ResourceNotFoundException {
        vendorMasterService.deleteVmId(id);
        return ResponseEntity.noContent().build();
    }
}
