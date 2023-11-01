package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.ValuationCategoryRequest;
import com.example.plantservice.dto.response.ValuationCategoryResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface ValuationCategoryService {

    List<ValuationCategoryResponse> getAllValuationCategory();

    ValuationCategoryResponse updateValuationCategory(Long id, ValuationCategoryRequest valuationCategoryRequest) throws ResourceNotFoundException, AlreadyExistsException;

    void deleteValuationCategory(Long id) throws ResourceNotFoundException;

    ValuationCategoryResponse saveValuationCategory(@Valid ValuationCategoryRequest valuationCategoryRequest) throws ResourceNotFoundException, AlreadyExistsException;

    ValuationCategoryResponse getValuationCategoryById(Long id) throws ResourceNotFoundException;

    ValuationCategoryResponse updateStatusUsingValuationCategoryId(Long id) throws ResourceNotFoundException;

    List<ValuationCategoryResponse> updateBulkStatusValuationCategoryId(List<Long> id);

}
