package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.TransportationGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TransportationGroupController {
    private final TransportationGroupService transportationGroupService;

    @PostMapping("/saveTg")
    public ResponseEntity<Object> saveTg(@Valid @RequestBody TransportationGroupRequest transportationGroupRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveTg").toUriString());
        TransportationGroupResponse saveTg = transportationGroupService.saveTg(transportationGroupRequest);
        return ResponseEntity.created(uri).body(saveTg);
    }

    @GetMapping("/getAllTg")
    public ResponseEntity<Object> getAllTg() {
        List<TransportationGroupResponse> allTg = transportationGroupService.getAllTg();
        return ResponseEntity.ok(allTg);
    }


    @GetMapping("/getTgById/{id}")
    public ResponseEntity<Object> getTgById(@PathVariable Long id) throws ResourceNotFoundException {
        TransportationGroupResponse dpById = transportationGroupService.getTgById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllTgTrue")
    public ResponseEntity<Object> listTgStatusTrue() {
        List<TransportationGroupResponse> groupResponseList = transportationGroupService.findAllStatusTrue();
        return ResponseEntity.ok(groupResponseList);
    }

    @PutMapping("/updateTg/{id}")
    public ResponseEntity<Object> updateTg(@PathVariable Long id, @Valid @RequestBody TransportationGroupRequest updateTransportationGroupRequest) throws ResourceNotFoundException, ResourceFoundException {
        TransportationGroupResponse updateTg = transportationGroupService.updateTg(id, updateTransportationGroupRequest);
        return ResponseEntity.ok(updateTg);
    }

    @DeleteMapping("/deleteTg/{id}")
    public ResponseEntity<Object> deleteTg(@PathVariable Long id) throws ResourceNotFoundException {
        transportationGroupService.deleteTgById(id);
        return ResponseEntity.noContent().build();
    }
}
