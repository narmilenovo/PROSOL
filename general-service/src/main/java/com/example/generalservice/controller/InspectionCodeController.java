package com.example.generalservice.controller;

import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.service.interfaces.InspectionCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class InspectionCodeController {
    private final InspectionCodeService inspectionCodeService;

    @PostMapping("/saveInCode")
    public ResponseEntity<Object> saveInCode(@Valid @RequestBody InspectionCodeRequest inspectionCodeRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveInCode").toUriString());
        InspectionCodeResponse inCode = inspectionCodeService.saveInCode(inspectionCodeRequest);
        return ResponseEntity.created(uri).body(inCode);
    }

    @GetMapping("/getAllInCode")
    public ResponseEntity<Object> getAllInCode() {
        List<InspectionCodeResponse> codeResponses = inspectionCodeService.getAllInCode();
        return ResponseEntity.ok(codeResponses);
    }


    @GetMapping("/getInCodeById/{id}")
    public ResponseEntity<Object> getInCodeById(@PathVariable Long id) throws ResourceNotFoundException {
        InspectionCodeResponse codeResponse = inspectionCodeService.getInCodeById(id);
        return ResponseEntity.ok(codeResponse);
    }

    @GetMapping("/getAllInCodeTrue")
    public ResponseEntity<Object> listInCodeStatusTrue() {
        List<InspectionCodeResponse> codeResponses = inspectionCodeService.findAllStatusTrue();
        return ResponseEntity.ok(codeResponses);
    }

    @PutMapping("/updateInCode/{id}")
    public ResponseEntity<Object> updateInCode(@PathVariable Long id, @Valid @RequestBody InspectionCodeRequest updateInspectionCodeRequest) throws ResourceNotFoundException, ResourceFoundException {
        InspectionCodeResponse codeResponse = inspectionCodeService.updateInCode(id, updateInspectionCodeRequest);
        return ResponseEntity.ok(codeResponse);
    }

    @DeleteMapping("/deleteInCode/{id}")
    public ResponseEntity<Object> deleteInCode(@PathVariable Long id) throws ResourceNotFoundException {
        inspectionCodeService.deleteInCodeId(id);
        return ResponseEntity.noContent().build();
    }
}
