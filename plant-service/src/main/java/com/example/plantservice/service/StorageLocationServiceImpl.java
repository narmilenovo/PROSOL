package com.example.plantservice.service;

import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.StorageLocation;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.repository.StorageLocationRepo;
import com.example.plantservice.service.interfaces.StorageLocationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageLocationServiceImpl implements StorageLocationService {
    private static final String STORAGE_LOCATION_NOT_FOUND_MESSAGE = null;

    private final StorageLocationRepo storageLocationRepo;

    private final PlantRepo plantRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<StorageLocationResponse> getAllStorageLocation() {
        List<StorageLocation> storageLocation = storageLocationRepo.findAll();
        return storageLocation.stream().map(this::mapToStorageLocationResponse).toList();
    }

    @Override
    public StorageLocationResponse updateStorageLocation(Long id, StorageLocationRequest storageLocationRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<StorageLocation> existstorageLocation = storageLocationRepo.findByStorageLocationTitle(storageLocationRequest.getStorageLocationTitle());
        if (existstorageLocation.isPresent()) {
            throw new AlreadyExistsException("StorageLocation with this name already exists");
        } else {

            StorageLocation existingStorageLocation = this.findStorageLocationById(id);
            modelMapper.map(storageLocationRequest, existingStorageLocation);
            storageLocationRepo.save(existingStorageLocation);
            return mapToStorageLocationResponse(existingStorageLocation);
        }
    }

    @Override
    public void deleteStorageLocation(Long id) throws ResourceNotFoundException {
        StorageLocation storageLocation = this.findStorageLocationById(id);
        storageLocationRepo.deleteById(storageLocation.getId());
    }

    @Override
    public StorageLocationResponse saveStorageLocation(StorageLocationRequest storageLocationRequest) throws  AlreadyExistsException {
        Optional<StorageLocation> existStorageLocation = storageLocationRepo.findByStorageLocationTitle(storageLocationRequest.getStorageLocationTitle());
        if (existStorageLocation.isPresent()&& !existStorageLocation.get().getStorageLocationTitle().equals(storageLocationRequest.getStorageLocationTitle())) {
            throw new AlreadyExistsException("StorageLocation with this name already exists");
        } else {

            StorageLocation storageLocation = modelMapper.map(storageLocationRequest, StorageLocation.class);
            storageLocation.setPlant(setToString(storageLocationRequest.getPlantId()));
            storageLocationRepo.save(storageLocation);
            return mapToStorageLocationResponse(storageLocation);
        }
    }

    private Plant setToString(String plantName) {
        Optional<Plant> fetchplantOptional = plantRepo.findByPlantName(plantName);
       return fetchplantOptional.orElse(null);
    }


    private StorageLocationResponse mapToStorageLocationResponse(StorageLocation storageLocation) {
        return modelMapper.map(storageLocation, StorageLocationResponse.class);
    }


    private StorageLocation findStorageLocationById(Long id) throws ResourceNotFoundException {
        Optional<StorageLocation> storageLocation = storageLocationRepo.findById(id);
        if (storageLocation.isEmpty()) {
            throw new ResourceNotFoundException(STORAGE_LOCATION_NOT_FOUND_MESSAGE);
        }
        return storageLocation.get();
    }



    @Override
    public StorageLocationResponse getStorageLocationById(Long id) throws ResourceNotFoundException {
        StorageLocation storageLocation = this.findStorageLocationById(id);
        return mapToStorageLocationResponse(storageLocation);
    }

    @Override
    public List<StorageLocationResponse> updateBulkStatusStorageLocationId(List<Long> id) {
        List<StorageLocation> existingStorageLocation = storageLocationRepo.findAllById(id);
        for (StorageLocation storageLocation : existingStorageLocation) {
            storageLocation.setStatus(!storageLocation.getStatus());
        }
        storageLocationRepo.saveAll(existingStorageLocation);
        return existingStorageLocation.stream().map(this::mapToStorageLocationResponse).toList();
    }

    @Override
    public StorageLocationResponse updateStatusUsingStorageLocationId(Long id) throws ResourceNotFoundException {
        StorageLocation existingStorageLocation = this.findStorageLocationById(id);
        existingStorageLocation.setStatus(!existingStorageLocation.getStatus());
        storageLocationRepo.save(existingStorageLocation);
        return mapToStorageLocationResponse(existingStorageLocation);
    }
}
