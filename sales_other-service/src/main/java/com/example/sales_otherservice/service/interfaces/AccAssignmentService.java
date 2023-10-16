package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.AccAssignmentRequest;
import com.example.sales_otherservice.dto.response.AccAssignmentResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface AccAssignmentService {
    AccAssignmentResponse saveAcc(AccAssignmentRequest accAssignmentRequest) throws ResourceFoundException;

    List<AccAssignmentResponse> getAllAcc();

    AccAssignmentResponse getAccById(Long id) throws ResourceNotFoundException;

    List<AccAssignmentResponse> findAllStatusTrue();

    AccAssignmentResponse updateAcc(Long id, AccAssignmentRequest updateAccAssignmentRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteAccId(Long id) throws ResourceNotFoundException;
}
