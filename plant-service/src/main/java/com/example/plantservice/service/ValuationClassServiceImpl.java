package com.example.plantservice.service;

import com.example.plantservice.config.MaterialTypeClient;
import com.example.plantservice.dto.request.ValuationClassRequest;
import com.example.plantservice.dto.response.ValuationClassResponse;
import com.example.plantservice.dto.response.ValuationMaterialResponse;
import com.example.plantservice.entity.ValuationClass;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.ValuationClassRepo;
import com.example.plantservice.service.interfaces.ValuationClassService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValuationClassServiceImpl implements ValuationClassService {

    private final ValuationClassRepo valuationClassRepo;

    private final ModelMapper modelMapper;
    private final MaterialTypeClient materialTypeClient;
    public static final String VALUATION_CLASS_NOT_FOUND_MESSAGE = null;


    @Override
    public List<ValuationClassResponse> getAllValuationClass() {
        List<ValuationClass> valuationClass = valuationClassRepo.findAll();
        return valuationClass.stream().map(this::mapToValuationClassResponse).toList();
    }

    @Override
    public List<ValuationMaterialResponse> getAllValuationClassByMaterial() throws ResourceNotFoundException {
        List<ValuationClass> valuationClasses = valuationClassRepo.findAll();
        List<ValuationMaterialResponse> responseList = new ArrayList<>();
        for (ValuationClass valuationClass : valuationClasses) {
            ValuationMaterialResponse valuationMaterialResponse = mapToValuationMaterialResponse(valuationClass);
            responseList.add(valuationMaterialResponse);
        }
        return responseList;
    }

    @Override
    public ValuationClassResponse updateValuationClass(Long id, ValuationClassRequest valuationClassRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<ValuationClass> existValuationClassName = valuationClassRepo.findByValuationClassName(valuationClassRequest.getValuationClassName());
        if (existValuationClassName.isPresent() && !existValuationClassName.get().getValuationClassName().equals(valuationClassRequest.getValuationClassName())) {
            throw new AlreadyExistsException("ValuationClass with this name already exists");
        } else {
            ValuationClass existingValuationClass = this.findValuationClassById(id);
            modelMapper.map(valuationClassRequest, existingValuationClass);
            valuationClassRepo.save(existingValuationClass);
            return mapToValuationClassResponse(existingValuationClass);
        }
    }

    public void deleteValuationClass(Long id) throws ResourceNotFoundException {
        ValuationClass valuationClass = this.findValuationClassById(id);
        valuationClassRepo.deleteById(valuationClass.getId());
    }

    @Override
    public ValuationClassResponse saveValuationClass(ValuationClassRequest valuationClassRequest) throws  AlreadyExistsException {

        Optional<ValuationClass> existValuationClassName = valuationClassRepo.findByValuationClassName(valuationClassRequest.getValuationClassName());
        if (existValuationClassName.isPresent()) {
            throw new AlreadyExistsException("ValuationClass with this name already exists");
        } else {
            ValuationClass valuationClass = modelMapper.map(valuationClassRequest, ValuationClass.class);
            valuationClassRepo.save(valuationClass);
            return mapToValuationClassResponse(valuationClass);
        }
    }


    private ValuationClassResponse mapToValuationClassResponse(ValuationClass valuationClass) {
        return modelMapper.map(valuationClass, ValuationClassResponse.class);
    }

    private ValuationClass findValuationClassById(Long id) throws ResourceNotFoundException {
        Optional<ValuationClass> valuationClass = valuationClassRepo.findById(id);
        if (valuationClass.isEmpty()) {
            throw new ResourceNotFoundException(VALUATION_CLASS_NOT_FOUND_MESSAGE);
        }
        return valuationClass.get();
    }


    @Override
    public ValuationClassResponse getValuationClassById(Long id) throws ResourceNotFoundException {
        ValuationClass valuationClass = this.findValuationClassById(id);
        return mapToValuationClassResponse(valuationClass);
    }

    @Override
    public List<ValuationClassResponse> updateBulkStatusValuationClassId(List<Long> id) {
        List<ValuationClass> existingValuationClass = valuationClassRepo.findAllById(id);
        for (ValuationClass valuationClass : existingValuationClass) {
            valuationClass.setValuationClassStatus(!valuationClass.getValuationClassStatus());
        }
        valuationClassRepo.saveAll(existingValuationClass);
        return existingValuationClass.stream().map(this::mapToValuationClassResponse).toList();
    }

    @Override
    public ValuationClassResponse updateStatusUsingValuationClassId(Long id) throws ResourceNotFoundException {
        ValuationClass existingValuationClass = this.findValuationClassById(id);
        existingValuationClass.setValuationClassStatus(!existingValuationClass.getValuationClassStatus());
        valuationClassRepo.save(existingValuationClass);
        return mapToValuationClassResponse(existingValuationClass);
    }

    private ValuationMaterialResponse mapToValuationMaterialResponse(ValuationClass valuationClass) throws ResourceNotFoundException {
        ValuationMaterialResponse valuationMaterialResponse = modelMapper.map(valuationClass, ValuationMaterialResponse.class);
        valuationMaterialResponse.setMaterial(materialTypeClient.getMaterialById(valuationClass.getId()));
        return valuationMaterialResponse;
    }

}
