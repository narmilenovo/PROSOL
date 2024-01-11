package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface PurchasingValueKeyService {
	PurchasingValueKeyResponse savePvk(PurchasingValueKeyRequest purchasingValueKeyRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<PurchasingValueKeyResponse> getAllPvk();

	PurchasingValueKeyResponse getPvkById(Long id) throws ResourceNotFoundException;

	List<PurchasingValueKeyResponse> findAllStatusTrue();

	PurchasingValueKeyResponse updatePvk(Long id, PurchasingValueKeyRequest updatePurchasingValueKeyRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deletePvkById(Long id) throws ResourceNotFoundException;

	void deleteBatchPvk(List<Long> ids);
}
