package com.example.plantservice.controller;

import com.example.plantservice.dto.request.DepartmentRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.service.interfaces.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping("/getAllDepartment")
    public ResponseEntity<Object> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @PutMapping("/updateDepartment/{id}")
    public ResponseEntity<Object> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest departmentRequest) throws ResourceNotFoundException, AlreadyExistsException {
        DepartmentResponse updateDepartment = departmentService.updateDepartment(id, departmentRequest);
        return ResponseEntity.ok().body(updateDepartment);
    }

    @DeleteMapping("/deleteDepartment/{id}")
    public ResponseEntity<String> deleteDepartment(@PathVariable Long id) throws ResourceNotFoundException {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().body("Department of '" + id + "' is deleted");
    }

    @PostMapping("/saveDepartment")
    public ResponseEntity<Object> saveDepartment(@Valid @RequestBody DepartmentRequest departmentRequest) throws ResourceNotFoundException, AlreadyExistsException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveDepartment").toUriString());
        DepartmentResponse savedDepartment = departmentService.saveDepartment(departmentRequest);
        return ResponseEntity.created(uri).body(savedDepartment);
    }

    @GetMapping("/getDepartmentById/{id}")
    public ResponseEntity<Object> getDepartmentById(@PathVariable Long id) throws ResourceNotFoundException {
        DepartmentResponse foundDepartment = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(foundDepartment);
    }

    @PatchMapping("/updateDepartmentStatusById/{id}")
    public ResponseEntity<Object> updateDepartmentStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        DepartmentResponse response = departmentService.updateStatusUsingDepartmentId(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/updateBulkStatusDepartmentId/{id}")
    public ResponseEntity<Object> updateBulkStatusDepartmentId(@PathVariable List<Long> id) {
        List<DepartmentResponse> responseList = departmentService.updateBulkStatusDepartmentId(id);
        return ResponseEntity.ok(responseList);
    }
}
