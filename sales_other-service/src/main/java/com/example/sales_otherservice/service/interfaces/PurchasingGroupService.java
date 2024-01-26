package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface PurchasingGroupService {
	PurchasingGroupResponse savePg(PurchasingGroupRequest purchasingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	PurchasingGroupResponse getPgById(Long id) throws ResourceNotFoundException;

	List<PurchasingGroupResponse> getAllPg();

	List<PurchasingGroupResponse> findAllStatusTrue();

	PurchasingGroupResponse updatePg(Long id, PurchasingGroupRequest updatePurchasingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	PurchasingGroupResponse updatePgStatus(Long id) throws ResourceNotFoundException;

	List<PurchasingGroupResponse> updateBatchPgStatus(List<Long> ids) throws ResourceNotFoundException;

	void deletePgById(Long id) throws ResourceNotFoundException;

	void deleteBatchPg(List<Long> ids) throws ResourceNotFoundException;

}
