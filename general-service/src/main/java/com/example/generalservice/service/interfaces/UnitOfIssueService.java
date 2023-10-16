package com.example.generalservice.service.interfaces;

import com.example.generalservice.dto.request.UnitOfIssueRequest;
import com.example.generalservice.dto.response.UnitOfIssueResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface UnitOfIssueService {
    UnitOfIssueResponse saveUOI(UnitOfIssueRequest unitOfIssueRequest) throws ResourceFoundException;

    List<UnitOfIssueResponse> getAllUOI();

    UnitOfIssueResponse getUOIById(Long id) throws ResourceNotFoundException;

    List<UnitOfIssueResponse> findAllStatusTrue();

    UnitOfIssueResponse updateUOI(Long id, UnitOfIssueRequest updateUnitOfIssueRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteUOIId(Long id) throws ResourceNotFoundException;
}
