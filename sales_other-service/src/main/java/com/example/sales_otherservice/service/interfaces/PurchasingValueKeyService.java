package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface PurchasingValueKeyService {
	PurchasingValueKeyResponse savePvk(PurchasingValueKeyRequest purchasingValueKeyRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	PurchasingValueKeyResponse getPvkById(Long id) throws ResourceNotFoundException;

	List<PurchasingValueKeyResponse> getAllPvk();

	List<PurchasingValueKeyResponse> findAllStatusTrue();

	PurchasingValueKeyResponse updatePvk(Long id, PurchasingValueKeyRequest updatePurchasingValueKeyRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	PurchasingValueKeyResponse updatePvkStatus(Long id) throws ResourceNotFoundException;

	List<PurchasingValueKeyResponse> updateBatchPvkStatus(List<Long> ids) throws ResourceNotFoundException;

	void deletePvkById(Long id) throws ResourceNotFoundException;

	void deleteBatchPvk(List<Long> ids) throws ResourceNotFoundException;

}
