package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.LoadingGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoadingGroupController {
    private final LoadingGroupService loadingGroupService;

    @PostMapping("/saveLg")
    public ResponseEntity<Object> saveLg(@Valid @RequestBody LoadingGroupRequest loadingGroupRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveLg").toUriString());
        LoadingGroupResponse saveLg = loadingGroupService.saveLg(loadingGroupRequest);
        return ResponseEntity.created(uri).body(saveLg);
    }

    @GetMapping("/getAllLg")
    public ResponseEntity<Object> getAllLg() {
        List<LoadingGroupResponse> allLg = loadingGroupService.getAllLg();
        return ResponseEntity.ok(allLg);
    }


    @GetMapping("/getLgById/{id}")
    public ResponseEntity<Object> getLgById(@PathVariable Long id) throws ResourceNotFoundException {
        LoadingGroupResponse dpById = loadingGroupService.getLgById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllLgTrue")
    public ResponseEntity<Object> listLgStatusTrue() {
        List<LoadingGroupResponse> groupResponses = loadingGroupService.findAllStatusTrue();
        return ResponseEntity.ok(groupResponses);
    }

    @PutMapping("/updateLg/{id}")
    public ResponseEntity<Object> updateLg(@PathVariable Long id, @Valid @RequestBody LoadingGroupRequest updateLoadingGroupRequest) throws ResourceNotFoundException, ResourceFoundException {
        LoadingGroupResponse updateLg = loadingGroupService.updateLg(id, updateLoadingGroupRequest);
        return ResponseEntity.ok(updateLg);
    }

    @DeleteMapping("/deleteLg/{id}")
    public ResponseEntity<Object> deleteLg(@PathVariable Long id) throws ResourceNotFoundException {
        loadingGroupService.deleteLgById(id);
        return ResponseEntity.noContent().build();
    }
}
