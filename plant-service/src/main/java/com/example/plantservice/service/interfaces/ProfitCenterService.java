package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.ProfitCenterRequest;
import com.example.plantservice.dto.response.ProfitCenterResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface ProfitCenterService {

    List<ProfitCenterResponse> getAllProfitCenter();

    ProfitCenterResponse updateProfitCenter(Long id, ProfitCenterRequest profitCenterRequest) throws ResourceNotFoundException, AlreadyExistsException;

    void deleteProfitCenter(Long id) throws ResourceNotFoundException;

    ProfitCenterResponse saveProfitCenter(@Valid ProfitCenterRequest profitCenterRequest) throws ResourceNotFoundException, AlreadyExistsException;

    ProfitCenterResponse getProfitCenterById(Long id) throws ResourceNotFoundException;

    ProfitCenterResponse updateStatusUsingProfitCenterId(Long id) throws ResourceNotFoundException;

    List<ProfitCenterResponse> updateBulkStatusProfitCenterId(List<Long> id);

    

}
