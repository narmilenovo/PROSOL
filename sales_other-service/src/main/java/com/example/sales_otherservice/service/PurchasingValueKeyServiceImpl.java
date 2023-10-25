package com.example.sales_otherservice.service;

import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.entity.PurchasingValueKey;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.PurchasingValueKeyRepository;
import com.example.sales_otherservice.service.interfaces.PurchasingValueKeyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchasingValueKeyServiceImpl implements PurchasingValueKeyService {
    private final PurchasingValueKeyRepository purchasingValueKeyRepository;
    private final ModelMapper modelMapper;

    @Override
    public PurchasingValueKeyResponse savePvk(PurchasingValueKeyRequest purchasingValueKeyRequest) throws ResourceFoundException {
        String pvkCode = purchasingValueKeyRequest.getPvkCode();
        String pvkName = purchasingValueKeyRequest.getPvkName();
        boolean exists = purchasingValueKeyRepository.existsByPvkCodeOrPvkName(pvkCode, pvkName);
        if (!exists) {

            PurchasingValueKey valueKey = modelMapper.map(purchasingValueKeyRequest, PurchasingValueKey.class);
            PurchasingValueKey savedValueKey = purchasingValueKeyRepository.save(valueKey);
            return mapToPurchasingValueKeyResponse(savedValueKey);
        }
        throw new ResourceFoundException("Purchasing Value Key Already exist");
    }

    @Override
    @Cacheable("pvk")
    public List<PurchasingValueKeyResponse> getAllPvk() {
        List<PurchasingValueKey> purchasingValueKeys = purchasingValueKeyRepository.findAll();
        return purchasingValueKeys.stream()
                .sorted(Comparator.comparing(PurchasingValueKey::getId))
                .map(this::mapToPurchasingValueKeyResponse)
                .toList();
    }

    @Override
    @Cacheable("pvk")
    public PurchasingValueKeyResponse getPvkById(Long id) throws ResourceNotFoundException {
        PurchasingValueKey valueKey = this.findPvkById(id);
        return mapToPurchasingValueKeyResponse(valueKey);
    }

    @Override
    @Cacheable("pvk")
    public List<PurchasingValueKeyResponse> findAllStatusTrue() {
        List<PurchasingValueKey> purchasingValueKeys = purchasingValueKeyRepository.findAllByPvkStatusIsTrue();
        return purchasingValueKeys.stream()
                .sorted(Comparator.comparing(PurchasingValueKey::getId))
                .map(this::mapToPurchasingValueKeyResponse)
                .toList();
    }

    @Override
    public PurchasingValueKeyResponse updatePvk(Long id, PurchasingValueKeyRequest updatePurchasingValueKeyRequest) throws ResourceNotFoundException, ResourceFoundException {
        String pvkCode = updatePurchasingValueKeyRequest.getPvkCode();
        String pvkName = updatePurchasingValueKeyRequest.getPvkName();
        PurchasingValueKey existingValueKey = this.findPvkById(id);
        boolean exists = purchasingValueKeyRepository.existsByPvkCodeAndIdNotOrPvkNameAndIdNot(pvkCode, id, pvkName, id);
        if (!exists) {
            modelMapper.map(updatePurchasingValueKeyRequest, existingValueKey);
            PurchasingValueKey updatedValueKey = purchasingValueKeyRepository.save(existingValueKey);
            return mapToPurchasingValueKeyResponse(updatedValueKey);
        }
        throw new ResourceFoundException("Purchasing Value Key Already exist");
    }

    @Override
    public void deletePvkById(Long id) throws ResourceNotFoundException {
        PurchasingValueKey valueKey = this.findPvkById(id);
        purchasingValueKeyRepository.deleteById(valueKey.getId());
    }

    private PurchasingValueKeyResponse mapToPurchasingValueKeyResponse(PurchasingValueKey purchasingValueKey) {
        return modelMapper.map(purchasingValueKey, PurchasingValueKeyResponse.class);
    }

    private PurchasingValueKey findPvkById(Long id) throws ResourceNotFoundException {
        Optional<PurchasingValueKey> valueKey = purchasingValueKeyRepository.findById(id);
        if (valueKey.isEmpty()) {
            throw new ResourceNotFoundException("Purchasing Value Key not found with this Id");
        }
        return valueKey.get();
    }
}
