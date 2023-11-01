package com.example.plantservice.service;

import com.example.plantservice.dto.request.VarianceKeyRequest;
import com.example.plantservice.dto.response.VarianceKeyResponse;
import com.example.plantservice.entity.VarianceKey;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.repository.VarianceKeyRepo;
import com.example.plantservice.service.interfaces.VarianceKeyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VarianceKeyServiceImpl implements VarianceKeyService {

    private final VarianceKeyRepo varianceKeyRepo;

    private final ModelMapper modelMapper;

    public static final String VARIANCE_KEY_NOT_FOUND_MESSAGE = null;


    @Override
    public List<VarianceKeyResponse> getAllVarianceKey() {
        List<VarianceKey> valuationCategory = varianceKeyRepo.findAll();
        return valuationCategory.stream().map(this::mapToVarianceKeyResponse).toList();
    }

    @Override
    public VarianceKeyResponse updateVarianceKey(Long id, VarianceKeyRequest varianceKeyRequest) throws ResourceNotFoundException, AlreadyExistsException {

        Optional<VarianceKey> existVarianceKeyName = varianceKeyRepo.findByVarianceKeyName(varianceKeyRequest.getVarianceKeyName());
        if (existVarianceKeyName.isPresent() && !existVarianceKeyName.get().getVarianceKeyName().equals(varianceKeyRequest.getVarianceKeyName())) {
            throw new AlreadyExistsException("VarianceKey with this name already exists");
        } else {
            VarianceKey existingVarianceKey = this.findVarianceKeyById(id);
            modelMapper.map(varianceKeyRequest, existingVarianceKey);
            varianceKeyRepo.save(existingVarianceKey);
            return mapToVarianceKeyResponse(existingVarianceKey);
        }
    }

    public void deleteVarianceKey(Long id) throws ResourceNotFoundException {
        VarianceKey valuationCategory = this.findVarianceKeyById(id);
        varianceKeyRepo.deleteById(valuationCategory.getId());
    }

    @Override
    public VarianceKeyResponse saveVarianceKey(VarianceKeyRequest valuationCategoryRequest) throws  AlreadyExistsException {

        Optional<VarianceKey> existVarianceKeyName = varianceKeyRepo.findByVarianceKeyName(valuationCategoryRequest.getVarianceKeyName());
        if (existVarianceKeyName.isPresent() ) {
            throw new AlreadyExistsException("VarianceKey with this name already exists");
        } else {
            VarianceKey valuationCategory = modelMapper.map(valuationCategoryRequest, VarianceKey.class);
            varianceKeyRepo.save(valuationCategory);
            return mapToVarianceKeyResponse(valuationCategory);
        }
    }


    private VarianceKeyResponse mapToVarianceKeyResponse(VarianceKey valuationCategory) {
        return modelMapper.map(valuationCategory, VarianceKeyResponse.class);
    }

    private VarianceKey findVarianceKeyById(Long id) throws ResourceNotFoundException {
        Optional<VarianceKey> valuationCategory = varianceKeyRepo.findById(id);
        if (valuationCategory.isEmpty()) {
            throw new ResourceNotFoundException(VARIANCE_KEY_NOT_FOUND_MESSAGE);
        }
        return valuationCategory.get();
    }



    @Override
    public VarianceKeyResponse getVarianceKeyById(Long id) throws ResourceNotFoundException {
        VarianceKey valuationCategory = this.findVarianceKeyById(id);
        return mapToVarianceKeyResponse(valuationCategory);
    }

    @Override
    public List<VarianceKeyResponse> updateBulkStatusVarianceKeyId(List<Long> id) {
        List<VarianceKey> existingVarianceKey = varianceKeyRepo.findAllById(id);
        for (VarianceKey valuationCategory : existingVarianceKey) {
            valuationCategory.setVarianceKeyStatus(!valuationCategory.getVarianceKeyStatus());
        }
        varianceKeyRepo.saveAll(existingVarianceKey);
        return existingVarianceKey.stream().map(this::mapToVarianceKeyResponse).toList();
    }

    @Override
    public VarianceKeyResponse updateStatusUsingVarianceKeyId(Long id) throws ResourceNotFoundException {
        VarianceKey existingVarianceKey = this.findVarianceKeyById(id);
        existingVarianceKey.setVarianceKeyStatus(!existingVarianceKey.getVarianceKeyStatus());
        varianceKeyRepo.save(existingVarianceKey);
        return mapToVarianceKeyResponse(existingVarianceKey);
    }
}
