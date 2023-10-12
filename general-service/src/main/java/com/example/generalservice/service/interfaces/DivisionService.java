package com.example.generalservice.service.interfaces;

import com.example.generalservice.dto.request.DivisionRequest;
import com.example.generalservice.dto.response.DivisionResponse;
import com.example.generalservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface DivisionService {
    DivisionResponse saveDivision(DivisionRequest divisionRequest);

    DivisionResponse getDivisionById(Long id) throws ResourceNotFoundException;

    List<DivisionResponse> getAllDivision();

    List<DivisionResponse> findAllStatusTrue();

    DivisionResponse updateDivision(Long id, DivisionRequest updateDivisionRequest) throws ResourceNotFoundException;

    void deleteDivisionId(Long id) throws ResourceNotFoundException;


}
