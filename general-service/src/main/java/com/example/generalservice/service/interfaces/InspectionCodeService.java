package com.example.generalservice.service.interfaces;

import com.example.generalservice.dto.request.InspectionCodeRequest;
import com.example.generalservice.dto.response.InspectionCodeResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface InspectionCodeService {
    InspectionCodeResponse saveInCode(InspectionCodeRequest inspectionCodeRequest) throws ResourceFoundException;

    List<InspectionCodeResponse> getAllInCode();

    InspectionCodeResponse getInCodeById(Long id) throws ResourceNotFoundException;

    List<InspectionCodeResponse> findAllStatusTrue();

    InspectionCodeResponse updateInCode(Long id, InspectionCodeRequest updateInspectionCodeRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteInCodeId(Long id) throws ResourceNotFoundException;
}
