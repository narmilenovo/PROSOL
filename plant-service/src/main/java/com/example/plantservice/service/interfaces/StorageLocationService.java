package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface StorageLocationService {

    List<StorageLocationResponse> getAllStorageLocation();

    StorageLocationResponse updateStorageLocation(Long id, StorageLocationRequest storageLocationRequest) throws ResourceNotFoundException, AlreadyExistsException;

    StorageLocationResponse saveStorageLocation(@Valid StorageLocationRequest storageLocationRequest) throws ResourceNotFoundException, AlreadyExistsException;

    StorageLocationResponse getStorageLocationById(Long id) throws ResourceNotFoundException;

    void deleteStorageLocation(Long id) throws ResourceNotFoundException;

    StorageLocationResponse updateStatusUsingStorageLocationId(Long id) throws ResourceNotFoundException;

    List<StorageLocationResponse> updateBulkStatusStorageLocationId(List<Long> id);

}
