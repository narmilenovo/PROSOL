package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.TaxClassificationTypeRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationTypeResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface TaxClassificationTypeService {
    TaxClassificationTypeResponse saveTct(TaxClassificationTypeRequest taxClassificationClassRequest);

    List<TaxClassificationTypeResponse> getAllTct();

    TaxClassificationTypeResponse getTctById(Long id) throws ResourceNotFoundException;

    List<TaxClassificationTypeResponse> findAllStatusTrue();

    TaxClassificationTypeResponse updateTct(Long id, TaxClassificationTypeRequest updateTaxClassificationTypeRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteTctById(Long id) throws ResourceNotFoundException;
}
