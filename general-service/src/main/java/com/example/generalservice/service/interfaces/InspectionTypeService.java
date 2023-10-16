package com.example.generalservice.service.interfaces;

import com.example.generalservice.dto.request.InspectionTypeRequest;
import com.example.generalservice.dto.response.InspectionTypeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface InspectionTypeService {
    InspectionTypeResponse saveInType(InspectionTypeRequest inspectionTypeRequest) throws ResourceFoundException;

    List<InspectionTypeResponse> getAllInType();

    InspectionTypeResponse getInTypeById(Long id) throws ResourceNotFoundException;

    List<InspectionTypeResponse> findAllStatusTrue();

    InspectionTypeResponse updateInType(Long id, InspectionTypeRequest updateInspectionTypeRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteInTypeId(Long id) throws ResourceNotFoundException;
}
