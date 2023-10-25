package com.example.sales_otherservice.service;

import com.example.sales_otherservice.clients.PlantClient;
import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.entity.DeliveringPlant;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.DeliveringPlantRepository;
import com.example.sales_otherservice.service.interfaces.DeliveringPlantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveringPlantServiceImpl implements DeliveringPlantService {
    private final DeliveringPlantRepository deliveringPlantRepository;
    private final PlantClient plantClient;
    private final ModelMapper modelMapper;

    @Override
    public DeliveringPlantResponse saveDp(DeliveringPlantRequest deliveringPlantRequest) throws ResourceFoundException {
        String dpCode = deliveringPlantRequest.getDpCode();
        String dpName = deliveringPlantRequest.getDpName();
        boolean exists = deliveringPlantRepository.existsByDpCodeOrDpName(dpCode, dpName);
        if (!exists) {
            DeliveringPlant deliveringPlant = modelMapper.map(deliveringPlantRequest, DeliveringPlant.class);
            deliveringPlant.setId(null);
            DeliveringPlant savedPlant = deliveringPlantRepository.save(deliveringPlant);
            return mapToDeliveringPlantResponse(savedPlant);
        }
        throw new ResourceFoundException("Delivering Plant already exist");
    }

    @Override
    @Cacheable("dp")
    public List<DeliveringPlantResponse> getAllDp() {
        List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAll();
        return deliveringPlants.stream()
                .sorted(Comparator.comparing(DeliveringPlant::getId))
                .map(this::mapToDeliveringPlantResponse)
                .toList();
    }

    @Override
    @Cacheable("dp")
    public DeliveringPlantResponse getDpById(Long id) throws ResourceNotFoundException {
        DeliveringPlant deliveringPlant = this.findDpById(id);
        return mapToDeliveringPlantResponse(deliveringPlant);
    }

    @Override
    @Cacheable("dp")
    public List<DeliveringPlantResponse> findAllStatusTrue() {
        List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAllByDpStatusIsTrue();
        return deliveringPlants.stream()
                .sorted(Comparator.comparing(DeliveringPlant::getId))
                .map(this::mapToDeliveringPlantResponse)
                .toList();
    }

    @Override
    public DeliveringPlantResponse updateDp(Long id, DeliveringPlantRequest updateDeliveringPlantRequest) throws ResourceNotFoundException, ResourceFoundException {
        String dpCode = updateDeliveringPlantRequest.getDpCode();
        String dpName = updateDeliveringPlantRequest.getDpName();
        DeliveringPlant existingDeliveringPlant = this.findDpById(id);
        boolean exist = deliveringPlantRepository.existsByDpCodeAndIdNotOrDpNameAndIdNot(dpCode, id, dpName, id);
        if (!exist) {
            modelMapper.map(updateDeliveringPlantRequest, existingDeliveringPlant);
            DeliveringPlant updatedDeliveringPlant = deliveringPlantRepository.save(existingDeliveringPlant);
            return mapToDeliveringPlantResponse(updatedDeliveringPlant);
        }
        throw new ResourceFoundException("Delivering Plant already exist");
    }

    @Override
    public void deleteDpId(Long id) throws ResourceNotFoundException {
        DeliveringPlant deliveringPlant = this.findDpById(id);
        deliveringPlantRepository.deleteById(deliveringPlant.getId());
    }

    private DeliveringPlantResponse mapToDeliveringPlantResponse(DeliveringPlant deliveringPlant) {
        DeliveringPlantResponse deliveringPlantResponse = modelMapper.map(deliveringPlant, DeliveringPlantResponse.class);
        // Check if the id is null before getting the plant information
        if (deliveringPlant.getPlantId() != null) {
            deliveringPlantResponse.setPlant(plantClient.getPlantById(deliveringPlant.getPlantId()));
        }
        return deliveringPlantResponse;
    }


    private DeliveringPlant findDpById(Long id) throws ResourceNotFoundException {
        Optional<DeliveringPlant> deliveringPlant = deliveringPlantRepository.findById(id);
        if (deliveringPlant.isEmpty()) {
            throw new ResourceNotFoundException("Delivering Plant not found with this Id");
        }
        return deliveringPlant.get();
    }
}
