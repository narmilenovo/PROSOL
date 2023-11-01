package com.example.generalservice.controller;

import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.InspectionTypeService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InspectionTypeController {
    private final InspectionTypeService inspectionTypeService;

    @PostMapping("/saveInType")
    public ResponseEntity<Object> saveInCode(@Valid @RequestBody InspectionTypeRequest inspectionTypeRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveInType").toUriString());
        InspectionTypeResponse inCode = inspectionTypeService.saveInType(inspectionTypeRequest);
        return ResponseEntity.created(uri).body(inCode);
    }

    @GetMapping("/getAllInType")
    public ResponseEntity<Object> getAllInType() {
        List<InspectionTypeResponse> codeResponses = inspectionTypeService.getAllInType();
        return ResponseEntity.ok(codeResponses);
    }


    @GetMapping("/getInTypeById/{id}")
    public ResponseEntity<Object> getInTypeById(@PathVariable Long id) throws ResourceNotFoundException {
        InspectionTypeResponse codeResponse = inspectionTypeService.getInTypeById(id);
        return ResponseEntity.ok(codeResponse);
    }

    @GetMapping("/getAllInTypeTrue")
    public ResponseEntity<Object> listInTypeStatusTrue() {
        List<InspectionTypeResponse> codeResponses = inspectionTypeService.findAllStatusTrue();
        return ResponseEntity.ok(codeResponses);
    }

    @PutMapping("/updateInType/{id}")
    public ResponseEntity<Object> updateInType(@PathVariable Long id, @Valid @RequestBody InspectionTypeRequest updateInspectionTypeRequest) throws ResourceNotFoundException, ResourceFoundException {
        InspectionTypeResponse codeResponse = inspectionTypeService.updateInType(id, updateInspectionTypeRequest);
        return ResponseEntity.ok(codeResponse);
    }

    @DeleteMapping("/deleteInType/{id}")
    public ResponseEntity<Object> deleteInCode(@PathVariable Long id) throws ResourceNotFoundException {
        inspectionTypeService.deleteInTypeId(id);
        return ResponseEntity.noContent().build();
    }
}
