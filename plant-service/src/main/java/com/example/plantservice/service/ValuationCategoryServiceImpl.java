package com.example.plantservice.service;

import com.example.plantservice.dto.request.ValuationCategoryRequest;
import com.example.plantservice.dto.response.ValuationCategoryResponse;
import com.example.plantservice.entity.ValuationCategory;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.ValuationCategoryRepo;
import com.example.plantservice.service.interfaces.ValuationCategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ValuationCategoryServiceImpl implements ValuationCategoryService {

    private final ValuationCategoryRepo valuationCategoryRepo;

    private final ModelMapper modelMapper;

    public static final String VALUATION_CATAGORY_NOT_FOUND_MESSAGE = null;


    @Override
    public List<ValuationCategoryResponse> getAllValuationCategory() {
        List<ValuationCategory> valuationCategory = valuationCategoryRepo.findAll();
        return valuationCategory.stream().map(this::mapToValuationCategoryResponse).toList();
    }

    @Override
    public ValuationCategoryResponse updateValuationCategory(Long id, ValuationCategoryRequest valuationCategoryRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<ValuationCategory> existValuationCategoryName = valuationCategoryRepo.findByValuationCategoryName(valuationCategoryRequest.getValuationCategoryName());
        if (existValuationCategoryName.isPresent()&& !existValuationCategoryName.get().getValuationCategoryName().equals(valuationCategoryRequest.getValuationCategoryName())) {
            throw new AlreadyExistsException("ValuationCategory with this name already exists");
        } else {
            ValuationCategory existingValuationCategory = this.findValuationCategoryById(id);
            modelMapper.map(valuationCategoryRequest, existingValuationCategory);
            valuationCategoryRepo.save(existingValuationCategory);
            return mapToValuationCategoryResponse(existingValuationCategory);
        }
    }

    public void deleteValuationCategory(Long id) throws ResourceNotFoundException {
        ValuationCategory valuationCategory = this.findValuationCategoryById(id);
        valuationCategoryRepo.deleteById(valuationCategory.getId());
    }

    @Override
    public ValuationCategoryResponse saveValuationCategory(ValuationCategoryRequest valuationCategoryRequest) throws  AlreadyExistsException {

        Optional<ValuationCategory> existValuationCategoryName = valuationCategoryRepo.findByValuationCategoryName(valuationCategoryRequest.getValuationCategoryName());
        if (existValuationCategoryName.isPresent() ) {
            throw new AlreadyExistsException("ValuationCategory with this name already exists");
        } else {
            ValuationCategory valuationCategory = modelMapper.map(valuationCategoryRequest, ValuationCategory.class);
            valuationCategoryRepo.save(valuationCategory);
            return mapToValuationCategoryResponse(valuationCategory);
        }
    }


    private ValuationCategoryResponse mapToValuationCategoryResponse(ValuationCategory valuationCategory) {
        return modelMapper.map(valuationCategory, ValuationCategoryResponse.class);
    }

    private ValuationCategory findValuationCategoryById(Long id) throws ResourceNotFoundException {
        Optional<ValuationCategory> valuationCategory = valuationCategoryRepo.findById(id);
        if (valuationCategory.isEmpty()) {
            throw new ResourceNotFoundException(VALUATION_CATAGORY_NOT_FOUND_MESSAGE);
        }
        return valuationCategory.get();
    }



    @Override
    public ValuationCategoryResponse getValuationCategoryById(Long id) throws ResourceNotFoundException {
        ValuationCategory valuationCategory = this.findValuationCategoryById(id);
        return mapToValuationCategoryResponse(valuationCategory);
    }

    @Override
    public List<ValuationCategoryResponse> updateBulkStatusValuationCategoryId(List<Long> id) {
        List<ValuationCategory> existingValuationCategory = valuationCategoryRepo.findAllById(id);
        for (ValuationCategory valuationCategory : existingValuationCategory) {
            valuationCategory.setValuationCategoryStatus(!valuationCategory.getValuationCategoryStatus());
        }
        valuationCategoryRepo.saveAll(existingValuationCategory);
        return existingValuationCategory.stream().map(this::mapToValuationCategoryResponse).toList();
    }

    @Override
    public ValuationCategoryResponse updateStatusUsingValuationCategoryId(Long id) throws ResourceNotFoundException {
        ValuationCategory existingValuationCategory = this.findValuationCategoryById(id);
        existingValuationCategory.setValuationCategoryStatus(!existingValuationCategory.getValuationCategoryStatus());
        valuationCategoryRepo.save(existingValuationCategory);
        return mapToValuationCategoryResponse(existingValuationCategory);
    }
}
