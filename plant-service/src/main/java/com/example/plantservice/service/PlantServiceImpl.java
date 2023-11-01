package com.example.plantservice.service;


import com.example.plantservice.dto.request.PlantRequest;
import com.example.plantservice.dto.response.PlantResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.service.interfaces.PlantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {
    private static final String PLANT_NOT_FOUND_MESSAGE = null;
    private final PlantRepo plantRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<PlantResponse> getAllPlants() {
        List<Plant> plant = plantRepo.findAll();
        return plant.stream().map(this::mapToPlantResponse).toList();
    }

    @Override
    public PlantResponse updatePlant(Long id, PlantRequest plantRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<Plant> existPlantName = plantRepo.findByPlantName(plantRequest.getPlantName());
        if (existPlantName.isPresent()  && !existPlantName.get().getPlantName().equals(plantRequest.getPlantName())) {
            throw new AlreadyExistsException("Plant with this name already exists");
        } else {
            Plant existingPlant = this.findPlantById(id);
            modelMapper.map(plantRequest, existingPlant);
            plantRepo.save(existingPlant);
            return mapToPlantResponse(existingPlant);
        }
    }

    public void deletePlant(Long id) throws ResourceNotFoundException {
        Plant plant = this.findPlantById(id);
        plantRepo.deleteById(plant.getId());
    }

    @Override
    public PlantResponse savePlant(PlantRequest plantRequest) throws AlreadyExistsException {
        Optional<Plant> existPlantName = plantRepo.findByPlantName(plantRequest.getPlantName());

        if (existPlantName.isEmpty()) {
            Plant plant = modelMapper.map(plantRequest, Plant.class);
            plantRepo.save(plant);
            return mapToPlantResponse(plant);
        }
            throw new AlreadyExistsException("Plant with this name already exists");
    }


    private PlantResponse mapToPlantResponse(Plant plant) {
        return modelMapper.map(plant, PlantResponse.class);
    }

    private Plant findPlantById(Long plantId) throws ResourceNotFoundException {
        Optional<Plant> plant = plantRepo.findById(plantId);
        if (plant.isEmpty()) {
            throw new ResourceNotFoundException(PLANT_NOT_FOUND_MESSAGE);
        }
        return plant.get();
    }



    @Override
    public PlantResponse getPlantById(Long plantId) throws ResourceNotFoundException {
        Plant plant = this.findPlantById(plantId);
        return mapToPlantResponse(plant);
    }

    @Override
    public PlantResponse getPlantByName(String name) throws ResourceNotFoundException {
        Plant plant = this.findPlantByName(name);
        return mapToPlantResponse(plant);
    }

    private Plant findPlantByName(String name) throws ResourceNotFoundException{
        Optional<Plant> plant = plantRepo.findByPlantName(name);
        if (plant.isEmpty()) {
            throw new ResourceNotFoundException(PLANT_NOT_FOUND_MESSAGE);
        }
        return plant.get();
    }

    @Override
    public List<PlantResponse> updateBulkStatusPlantId(List<Long> id) {
        List<Plant> existingPlant = plantRepo.findAllById(id);
        for (Plant plant : existingPlant) {
            plant.setStatus(!plant.getStatus());
        }
        plantRepo.saveAll(existingPlant);
        return existingPlant.stream().map(this::mapToPlantResponse).toList();
    }

    @Override
    public PlantResponse updateStatusUsingPlantId(Long id) throws ResourceNotFoundException {
        Plant existingPlant = this.findPlantById(id);
        existingPlant.setStatus(!existingPlant.getStatus());
        plantRepo.save(existingPlant);
        return mapToPlantResponse(existingPlant);
    }
}
