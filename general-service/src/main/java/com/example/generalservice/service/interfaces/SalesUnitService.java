package com.example.generalservice.service.interfaces;

import com.example.generalservice.dto.request.SalesUnitRequest;
import com.example.generalservice.dto.response.SalesUnitResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface SalesUnitService {
    SalesUnitResponse saveSalesUnit(SalesUnitRequest salesUnitRequest);

    List<SalesUnitResponse> getAllSalesUnit();

    SalesUnitResponse getSalesUnitById(Long id) throws ResourceNotFoundException;

    List<SalesUnitResponse> findAllStatusTrue();

    SalesUnitResponse updateSalesUnit(Long id, SalesUnitRequest updateSalesUnitRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteSalesUnitId(Long id) throws ResourceNotFoundException;
}
