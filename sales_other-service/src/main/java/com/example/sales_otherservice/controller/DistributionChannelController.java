package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.DistributionChannelRequest;
import com.example.sales_otherservice.dto.response.DistributionChannelResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.DistributionChannelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DistributionChannelController {
    private final DistributionChannelService distributionChannelService;

    @PostMapping("/saveDc")
    public ResponseEntity<Object> saveDc(@Valid @RequestBody DistributionChannelRequest deliveringPlantRequest) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDc").toUriString());
        DistributionChannelResponse saveDc = distributionChannelService.saveDc(deliveringPlantRequest);
        return ResponseEntity.created(uri).body(saveDc);
    }

    @GetMapping("/getAllDc")
    public ResponseEntity<Object> getAllDc() {
        List<DistributionChannelResponse> allDc = distributionChannelService.getAllDc();
        return ResponseEntity.ok(allDc);
    }


    @GetMapping("/getDcById/{id}")
    public ResponseEntity<Object> getDcById(@PathVariable Long id) throws ResourceNotFoundException {
        DistributionChannelResponse dpById = distributionChannelService.getDcById(id);
        return ResponseEntity.ok(dpById);
    }

    @GetMapping("/getAllDcTrue")
    public ResponseEntity<Object> listDcStatusTrue() {
        List<DistributionChannelResponse> channelResponseList = distributionChannelService.findAllStatusTrue();
        return ResponseEntity.ok(channelResponseList);
    }

    @PutMapping("/updateDc/{id}")
    public ResponseEntity<Object> updateDc(@PathVariable Long id, @Valid @RequestBody DistributionChannelRequest updateDistributionChannelRequest) throws ResourceNotFoundException, ResourceFoundException {
        DistributionChannelResponse updateDc = distributionChannelService.updateDc(id, updateDistributionChannelRequest);
        return ResponseEntity.ok(updateDc);
    }

    @DeleteMapping("/deleteDc/{id}")
    public ResponseEntity<Object> deleteDc(@PathVariable Long id) throws ResourceNotFoundException {
        distributionChannelService.deleteDcId(id);
        return ResponseEntity.noContent().build();
    }
}
