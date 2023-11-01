package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.PriceControlRequest;
import com.example.plantservice.dto.response.PriceControlResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface PriceControlService {

    PriceControlResponse updatePriceControl(Long id, PriceControlRequest priceControlRequest) throws ResourceNotFoundException, AlreadyExistsException;

    PriceControlResponse savePriceControl(@Valid PriceControlRequest priceControlRequest) throws ResourceNotFoundException, AlreadyExistsException;

    void deletePriceControl(Long id) throws ResourceNotFoundException;

    PriceControlResponse getPriceControlById(Long id) throws ResourceNotFoundException;

    PriceControlResponse updateStatusUsingPriceControlId(Long id) throws ResourceNotFoundException;

    List<PriceControlResponse> updateBulkStatusPriceControlId(List<Long> id);

    List<PriceControlResponse> getAllPriceControl();

}
