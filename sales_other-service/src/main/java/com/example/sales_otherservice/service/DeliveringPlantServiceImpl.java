package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.entity.DeliveringPlant;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.DeliveringPlantRepository;
import com.example.sales_otherservice.service.interfaces.DeliveringPlantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeliveringPlantServiceImpl implements DeliveringPlantService {
    private final DeliveringPlantRepository deliveringPlantRepository;
    private final ModelMapper modelMapper;

    @Override
    public DeliveringPlantResponse saveDp(DeliveringPlantRequest deliveringPlantRequest) {
        DeliveringPlant deliveringPlant = modelMapper.map(deliveringPlantRequest, DeliveringPlant.class);
        DeliveringPlant savedPlant = deliveringPlantRepository.save(deliveringPlant);
        return mapToDeliveringPlantResponse(savedPlant);
    }

    @Override
    public List<DeliveringPlantResponse> getAllDp() {
        List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAll();
        return deliveringPlants.stream().map(this::mapToDeliveringPlantResponse).toList();
    }

    @Override
    public DeliveringPlantResponse getDpById(Long id) throws ResourceNotFoundException {
        DeliveringPlant deliveringPlant = this.findDpById(id);
        return mapToDeliveringPlantResponse(deliveringPlant);
    }

    @Override
    public List<DeliveringPlantResponse> findAllStatusTrue() {
        List<DeliveringPlant> deliveringPlants = deliveringPlantRepository.findAllByDpStatusIsTrue();
        return deliveringPlants.stream().map(this::mapToDeliveringPlantResponse).toList();
    }

    @Override
    public DeliveringPlantResponse updateDp(Long id, DeliveringPlantRequest updateDeliveringPlantRequest) throws ResourceNotFoundException, ResourceFoundException {
        DeliveringPlant existingDeliveringPlant = this.findDpById(id);
        String dpCode = updateDeliveringPlantRequest.getDpCode();
        boolean exist = deliveringPlantRepository.existsByDpCode(dpCode);
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
        return modelMapper.map(deliveringPlant, DeliveringPlantResponse.class);
    }

    private DeliveringPlant findDpById(Long id) throws ResourceNotFoundException {
        Optional<DeliveringPlant> deliveringPlant = deliveringPlantRepository.findById(id);
        if (deliveringPlant.isEmpty()) {
            throw new ResourceNotFoundException("Delivering Plant not found with this Id");
        }
        return deliveringPlant.get();
    }
}
