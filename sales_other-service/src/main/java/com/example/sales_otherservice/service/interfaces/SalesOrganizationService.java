package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface SalesOrganizationService {
	SalesOrganizationResponse saveSo(SalesOrganizationRequest salesOrganizationRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	SalesOrganizationResponse getSoById(@NonNull Long id) throws ResourceNotFoundException;

	List<SalesOrganizationResponse> getAllSo();

	List<SalesOrganizationResponse> findAllStatusTrue();

	SalesOrganizationResponse updateSo(@NonNull Long id, SalesOrganizationRequest updateSalesOrganizationRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	SalesOrganizationResponse updateSoStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<SalesOrganizationResponse> updateBatchSoStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteSoById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchSo(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
