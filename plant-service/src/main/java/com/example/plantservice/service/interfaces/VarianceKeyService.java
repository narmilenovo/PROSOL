package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.VarianceKeyRequest;
import com.example.plantservice.dto.response.VarianceKeyResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface VarianceKeyService {

    VarianceKeyResponse saveVarianceKey(@Valid VarianceKeyRequest varianceKeyRequest) throws ResourceNotFoundException, AlreadyExistsException;

    VarianceKeyResponse updateVarianceKey(Long id, VarianceKeyRequest varianceKeyRequest) throws ResourceNotFoundException, AlreadyExistsException;

    VarianceKeyResponse getVarianceKeyById(Long id) throws ResourceNotFoundException;

    void deleteVarianceKey(Long id) throws ResourceNotFoundException;

    VarianceKeyResponse updateStatusUsingVarianceKeyId(Long id) throws ResourceNotFoundException;

    List<VarianceKeyResponse> updateBulkStatusVarianceKeyId(List<Long> id);

    List<VarianceKeyResponse> getAllVarianceKey();


}
