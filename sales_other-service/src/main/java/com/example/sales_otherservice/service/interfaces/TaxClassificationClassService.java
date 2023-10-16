package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface TaxClassificationClassService {
    TaxClassificationClassResponse saveTcc(TaxClassificationClassRequest taxClassificationClassRequest) throws ResourceFoundException;

    List<TaxClassificationClassResponse> getAllTcc();

    TaxClassificationClassResponse getTccById(Long id) throws ResourceNotFoundException;

    List<TaxClassificationClassResponse> findAllStatusTrue();

    TaxClassificationClassResponse updateTcc(Long id, TaxClassificationClassRequest updateTaxClassificationClassRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteTccById(Long id) throws ResourceNotFoundException;
}
