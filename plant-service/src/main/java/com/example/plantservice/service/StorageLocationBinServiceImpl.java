package com.example.plantservice.service;

import com.example.plantservice.dto.request.StorageBinRequest;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.StorageBin;
import com.example.plantservice.entity.StorageLocation;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.repository.StorageLocationBinRepo;
import com.example.plantservice.repository.StorageLocationRepo;
import com.example.plantservice.service.interfaces.StorageLocationBinService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StorageLocationBinServiceImpl implements StorageLocationBinService {
    private static final String STORAGE_BIN_NOT_FOUND_MESSAGE = null;

    private final StorageLocationBinRepo storageLocationBinRepo;

    private final PlantRepo plantRepo;


    private final StorageLocationRepo storageLocationRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<StorageBinResponse> getAllStorageLocationBin() {
        List<StorageBin> storageLocationBin = storageLocationBinRepo.findAll();
        return storageLocationBin.stream().map(this::mapToStorageBinResponse).toList();
    }

    @Override
    public StorageBinResponse updateStorageLocationBin(Long id, StorageBinRequest storageBinRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<StorageBin> existstorageLocationBin = storageLocationBinRepo.findByTitle(storageBinRequest.getTitle());
        if (existstorageLocationBin.isPresent() && !existstorageLocationBin.get().getTitle().equals(storageBinRequest.getTitle())) {
            throw new AlreadyExistsException("StorageLocationBin with this name already exists");
        } else {

            StorageBin existStorageLocationBin1 = this.findStorageBinById(id);
            modelMapper.map(storageBinRequest, existStorageLocationBin1);
            storageLocationBinRepo.save(existStorageLocationBin1);
            return mapToStorageBinResponse(existStorageLocationBin1);
        }
    }

    @Override
    public void deleteStorageLocationBin(Long id) throws ResourceNotFoundException {
        StorageBin storageBin = this.findStorageBinById(id);
        storageLocationBinRepo.deleteById(storageBin.getId());
    }

    @Override
    public StorageBinResponse saveStorageLocation(StorageBinRequest storageBinRequest) throws  AlreadyExistsException {

        Optional<StorageBin> existStorageBin = storageLocationBinRepo.findByTitle(storageBinRequest.getTitle());
        if (existStorageBin.isPresent()) {
            throw new AlreadyExistsException("StorageLocationBin with this name already exists");
        } else {
            StorageBin storageBin = modelMapper.map(storageBinRequest, StorageBin.class);
            storageBin.setPlant(setToString(storageBinRequest.getPlantId()));
            storageBin.setStorageLocation(setToString1(storageBinRequest.getStorageLocationId()));
            StorageBin saved=storageLocationBinRepo.save(storageBin);
            return mapToStorageBinResponse(saved);
        }
    }

    private Plant setToString(Long plantId) {
        Optional<Plant> fetchplantOptional = plantRepo.findById(plantId);
        return fetchplantOptional.orElse(null);

    }

    private StorageLocation setToString1(Long id) {
        Optional<StorageLocation> fetchStorageOptional1 = storageLocationRepo.findById(id);
        return fetchStorageOptional1.orElse(null);

    }

    private StorageBinResponse mapToStorageBinResponse(StorageBin storageLocation) {
        return modelMapper.map(storageLocation, StorageBinResponse.class);
    }


    private StorageBin findStorageBinById(Long id) throws ResourceNotFoundException {
        Optional<StorageBin> storageBin = storageLocationBinRepo.findById(id);
        if (storageBin.isEmpty()) {
            throw new ResourceNotFoundException(STORAGE_BIN_NOT_FOUND_MESSAGE);
        }
        return storageBin.get();
    }

    @Override
    public StorageBinResponse getStorageLocationBinById(Long id) throws ResourceNotFoundException {
        StorageBin storageBin = this.findStorageBinById(id);
        return mapToStorageBinResponse(storageBin);
    }

    @Override
    public List<StorageBinResponse> updateBulkStatusStorageLocationBinId(List<Long> id) {
        List<StorageBin> existingStorageLocationBin = storageLocationBinRepo.findAllById(id);
        for (StorageBin storageLocationBin : existingStorageLocationBin) {
            storageLocationBin.setStatus(!storageLocationBin.getStatus());
        }
        storageLocationBinRepo.saveAll(existingStorageLocationBin);
        return existingStorageLocationBin.stream().map(this::mapToStorageBinResponse).toList();
    }
    @Override
    public StorageBinResponse updateStatusUsingStorageLocationBinId(Long id) throws ResourceNotFoundException {
        StorageBin existingStorageBin = this.findStorageBinById(id);
        existingStorageBin.setStatus(!existingStorageBin.getStatus());
        storageLocationBinRepo.save(existingStorageBin);
        return mapToStorageBinResponse(existingStorageBin);
    }
}
