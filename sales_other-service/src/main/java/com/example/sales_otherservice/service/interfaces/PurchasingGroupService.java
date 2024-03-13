package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface PurchasingGroupService {
	PurchasingGroupResponse savePg(PurchasingGroupRequest purchasingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	PurchasingGroupResponse getPgById(@NonNull Long id) throws ResourceNotFoundException;

	List<PurchasingGroupResponse> getAllPg();

	List<PurchasingGroupResponse> findAllStatusTrue();

	PurchasingGroupResponse updatePg(@NonNull Long id, PurchasingGroupRequest updatePurchasingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	PurchasingGroupResponse updatePgStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<PurchasingGroupResponse> updateBatchPgStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deletePgById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchPg(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
