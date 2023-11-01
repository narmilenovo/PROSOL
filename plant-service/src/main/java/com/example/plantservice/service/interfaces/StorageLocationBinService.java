package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.StorageBinRequest;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;

public interface StorageLocationBinService {

    List<StorageBinResponse> getAllStorageLocationBin();

    StorageBinResponse updateStorageLocationBin(Long id, StorageBinRequest storageBinRequest) throws ResourceNotFoundException, AlreadyExistsException;

    StorageBinResponse saveStorageLocation(@Valid StorageBinRequest storageBinRequest) throws ResourceNotFoundException, AlreadyExistsException;

    StorageBinResponse getStorageLocationBinById(Long id) throws ResourceNotFoundException;

    StorageBinResponse updateStatusUsingStorageLocationBinId(Long id) throws ResourceNotFoundException;

    List<StorageBinResponse> updateBulkStatusStorageLocationBinId(List<Long> id);

    void deleteStorageLocationBin(Long id) throws ResourceNotFoundException;

}
