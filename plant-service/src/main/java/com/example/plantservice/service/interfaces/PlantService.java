package com.example.plantservice.service.interfaces;

import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import jakarta.validation.Valid;

import java.util.List;


public interface PlantService {

    List<PlantResponse> getAllPlants();

    PlantResponse updatePlant(Long id, PlantRequest plantRequest) throws ResourceNotFoundException, AlreadyExistsException;

    void deletePlant(Long id) throws ResourceNotFoundException;

    PlantResponse savePlant(@Valid PlantRequest plantRequest) throws ResourceNotFoundException, AlreadyExistsException;

    PlantResponse getPlantById(Long plantId) throws ResourceNotFoundException;

    PlantResponse updateStatusUsingPlantId(Long id) throws ResourceNotFoundException;

    List<PlantResponse> updateBulkStatusPlantId(List<Long> id);


    PlantResponse getPlantByName(String name)throws ResourceNotFoundException;
}
