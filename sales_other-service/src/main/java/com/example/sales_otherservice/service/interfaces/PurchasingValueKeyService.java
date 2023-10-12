package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface PurchasingValueKeyService {
    PurchasingValueKeyResponse savePvk(PurchasingValueKeyRequest purchasingValueKeyRequest);

    List<PurchasingValueKeyResponse> getAllPvk();

    PurchasingValueKeyResponse getPvkById(Long id) throws ResourceNotFoundException;

    List<PurchasingValueKeyResponse> findAllStatusTrue();

    PurchasingValueKeyResponse updatePvk(Long id, PurchasingValueKeyRequest updatePurchasingValueKeyRequest) throws ResourceNotFoundException;

    void deletePvkById(Long id) throws ResourceNotFoundException;
}
