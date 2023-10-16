package com.example.sales_otherservice.controller;

import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.service.interfaces.AccAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccAssignmentController {
    private final AccAssignmentService accAssignmentService;

    @PostMapping("/saveAcc")
    public ResponseEntity<Object> saveAcc(@Valid @RequestBody AccAssignmentRequest accAssignmentRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveAcc").toUriString());
        AccAssignmentResponse accAssignmentResponse = accAssignmentService.saveAcc(accAssignmentRequest);
        return ResponseEntity.created(uri).body(accAssignmentResponse);
    }

    @GetMapping("/getAllAcc")
    public ResponseEntity<Object> getAllAcc() {
        List<AccAssignmentResponse> allAcc = accAssignmentService.getAllAcc();
        return ResponseEntity.ok(allAcc);
    }


    @GetMapping("/getAccById/{id}")
    public ResponseEntity<Object> getAccById(@PathVariable Long id) throws ResourceNotFoundException {
        AccAssignmentResponse accById = accAssignmentService.getAccById(id);
        return ResponseEntity.ok(accById);
    }

    @GetMapping("/getAllAccTrue")
    public ResponseEntity<Object> listAccStatusTrue() {
        List<AccAssignmentResponse> assignmentResponseList = accAssignmentService.findAllStatusTrue();
        return ResponseEntity.ok(assignmentResponseList);
    }

    @PutMapping("/updateAcc/{id}")
    public ResponseEntity<Object> updateAcc(@PathVariable Long id, @Valid @RequestBody AccAssignmentRequest updateAccAssignmentRequest) throws ResourceNotFoundException, ResourceFoundException {
        AccAssignmentResponse unitOfIssueResponse = accAssignmentService.updateAcc(id, updateAccAssignmentRequest);
        return ResponseEntity.ok(unitOfIssueResponse);
    }

    @DeleteMapping("/deleteAcc/{id}")
    public ResponseEntity<Object> deleteAcc(@PathVariable Long id) throws ResourceNotFoundException {
        accAssignmentService.deleteAccId(id);
        return ResponseEntity.noContent().build();
    }
}
