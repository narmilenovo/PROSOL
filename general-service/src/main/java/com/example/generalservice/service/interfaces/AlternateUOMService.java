package com.example.generalservice.service.interfaces;

import com.example.generalservice.dto.request.AlternateUOMRequest;
import com.example.generalservice.dto.response.AlternateUOMResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface AlternateUOMService {
    AlternateUOMResponse saveUom(AlternateUOMRequest alternateUOMRequest) throws ResourceFoundException;

    List<AlternateUOMResponse> getAllUom();

    AlternateUOMResponse getUomById(Long id) throws ResourceNotFoundException;

    List<AlternateUOMResponse> findAllStatusTrue();

    AlternateUOMResponse updateUom(Long id, AlternateUOMRequest updateAlternateUOMRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteUomId(Long id) throws ResourceNotFoundException;
}
