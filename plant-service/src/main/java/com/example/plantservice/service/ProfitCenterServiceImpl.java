package com.example.plantservice.service;

import com.example.plantservice.dto.request.ProfitCenterRequest;
import com.example.plantservice.dto.response.ProfitCenterResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.ProfitCenter;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.repository.ProfitCenterRepo;
import com.example.plantservice.service.interfaces.ProfitCenterService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfitCenterServiceImpl implements ProfitCenterService {
    private static final String PROFIT_CENTER_NOT_FOUND_MESSAGE = null;

    private final ProfitCenterRepo profitCenterRepo;

    private final PlantRepo plantRepo;

    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<ProfitCenterResponse> getAllProfitCenter() {
        List<ProfitCenter> profitCenter = profitCenterRepo.findAll();
        return profitCenter.stream().map(this::mapToProfitCenterResponse).toList();
    }

    @Override
    public ProfitCenterResponse updateProfitCenter(Long id, ProfitCenterRequest profitCenterRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<ProfitCenter> existProfitCenterName = profitCenterRepo.findByProfitCenterTitle(profitCenterRequest.getProfitCenterTitle());
        if (existProfitCenterName.isPresent()&& !existProfitCenterName.get().getProfitCenterTitle().equals(profitCenterRequest.getProfitCenterTitle())) {
            throw new AlreadyExistsException("ProfitCenter with this name already exists");
        } else {

            ProfitCenter existingProfitCenter = this.findProfitCenterById(id);
            modelMapper.map(profitCenterRequest, existingProfitCenter);
            existingProfitCenter.setPlant(setToPlant(profitCenterRequest.getPlantId()));
            profitCenterRepo.save(existingProfitCenter);
            return mapToProfitCenterResponse(existingProfitCenter);
        }
    }

    public void deleteProfitCenter(Long id) throws ResourceNotFoundException {
        ProfitCenter profitCenter = this.findProfitCenterById(id);
        profitCenterRepo.deleteById(profitCenter.getId());
    }

    @Override
    public ProfitCenterResponse saveProfitCenter(ProfitCenterRequest profitCenterRequest) throws  AlreadyExistsException {

        Optional<ProfitCenter> existProfitCenterName = profitCenterRepo.findByProfitCenterTitle(profitCenterRequest.getProfitCenterTitle());
        if (existProfitCenterName.isPresent()) {
            throw new AlreadyExistsException("ProfitCenter with this name already exists");
        } else {
            ProfitCenter profitCenter = modelMapper.map(profitCenterRequest, ProfitCenter.class);
            profitCenter.setPlant(setToPlant(profitCenterRequest.getPlantId()));
            profitCenterRepo.save(profitCenter);
            return mapToProfitCenterResponse(profitCenter);
        }
    }


    private Plant setToPlant(Long id) {
        Optional<Plant> fetchplantOptional = plantRepo.findById(id);
        return fetchplantOptional.orElse(null);
    }

    private ProfitCenterResponse mapToProfitCenterResponse(ProfitCenter profitCenter) {
        return modelMapper.map(profitCenter, ProfitCenterResponse.class);
    }


    private ProfitCenter findProfitCenterById(Long id) throws ResourceNotFoundException {
        Optional<ProfitCenter> profitCenter = profitCenterRepo.findById(id);
        if (profitCenter.isEmpty()) {
            throw new ResourceNotFoundException(PROFIT_CENTER_NOT_FOUND_MESSAGE);
        }
        return profitCenter.get();
    }



    @Override
    public ProfitCenterResponse getProfitCenterById(Long id) throws ResourceNotFoundException {
        ProfitCenter profitCenter = this.findProfitCenterById(id);
        return mapToProfitCenterResponse(profitCenter);
    }

    @Override
    public List<ProfitCenterResponse> updateBulkStatusProfitCenterId(List<Long> id) {
        List<ProfitCenter> existingProfitCenter = profitCenterRepo.findAllById(id);
        for (ProfitCenter profitCenter : existingProfitCenter) {
            profitCenter.setStatus(!profitCenter.getStatus());
        }
        profitCenterRepo.saveAll(existingProfitCenter);
        return existingProfitCenter.stream().map(this::mapToProfitCenterResponse).toList();
    }

    @Override
    public ProfitCenterResponse updateStatusUsingProfitCenterId(Long id) throws ResourceNotFoundException {
        ProfitCenter existingProfitCenter = this.findProfitCenterById(id);
        existingProfitCenter.setStatus(!existingProfitCenter.getStatus());
        profitCenterRepo.save(existingProfitCenter);
        return mapToProfitCenterResponse(existingProfitCenter);
    }
}
