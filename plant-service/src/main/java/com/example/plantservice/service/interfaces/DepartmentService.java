package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.DepartmentRequest;
import com.example.plantservice.dto.response.DepartmentResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface DepartmentService {

    List<DepartmentResponse> getAllDepartments();

    void deleteDepartment(Long id) throws ResourceNotFoundException;

    DepartmentResponse saveDepartment(@Valid DepartmentRequest departmentRequest) throws ResourceNotFoundException, AlreadyExistsException;

    DepartmentResponse getDepartmentById(Long id) throws ResourceNotFoundException;

    DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest) throws ResourceNotFoundException, AlreadyExistsException;

    DepartmentResponse updateStatusUsingDepartmentId(Long id) throws ResourceNotFoundException;

    List<DepartmentResponse> updateBulkStatusDepartmentId(List<Long> id);


}
