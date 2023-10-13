package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface DeliveringPlantService {
    DeliveringPlantResponse saveDp(DeliveringPlantRequest deliveringPlantRequest);

    List<DeliveringPlantResponse> getAllDp();

    DeliveringPlantResponse getDpById(Long id) throws ResourceNotFoundException;

    List<DeliveringPlantResponse> findAllStatusTrue();

    DeliveringPlantResponse updateDp(Long id, DeliveringPlantRequest updateDeliveringPlantRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteDpId(Long id) throws ResourceNotFoundException;
}