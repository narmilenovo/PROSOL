package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface PurchasingValueKeyService {
	PurchasingValueKeyResponse savePvk(PurchasingValueKeyRequest purchasingValueKeyRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	PurchasingValueKeyResponse getPvkById(@NonNull Long id) throws ResourceNotFoundException;

	List<PurchasingValueKeyResponse> getAllPvk();

	List<PurchasingValueKeyResponse> findAllStatusTrue();

	PurchasingValueKeyResponse updatePvk(@NonNull Long id, PurchasingValueKeyRequest updatePurchasingValueKeyRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	PurchasingValueKeyResponse updatePvkStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<PurchasingValueKeyResponse> updateBatchPvkStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deletePvkById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchPvk(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
