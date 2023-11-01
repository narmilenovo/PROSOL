package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.ValuationClassRequest;
import com.example.plantservice.dto.response.ValuationClassResponse;
import com.example.plantservice.dto.response.ValuationMaterialResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface ValuationClassService {

    ValuationClassResponse getValuationClassById(Long id) throws ResourceNotFoundException;

    ValuationClassResponse saveValuationClass(@Valid ValuationClassRequest valuationClassRequest) throws ResourceNotFoundException, AlreadyExistsException;

    ValuationClassResponse updateValuationClass(Long id, ValuationClassRequest valuationClassRequest) throws ResourceNotFoundException, AlreadyExistsException;

    void deleteValuationClass(Long id) throws ResourceNotFoundException;

    ValuationClassResponse updateStatusUsingValuationClassId(Long id) throws ResourceNotFoundException;

    List<ValuationClassResponse> updateBulkStatusValuationClassId(List<Long> id);

    List<ValuationClassResponse> getAllValuationClass();

    List<ValuationMaterialResponse> getAllValuationClassByMaterial() throws ResourceNotFoundException;
}
