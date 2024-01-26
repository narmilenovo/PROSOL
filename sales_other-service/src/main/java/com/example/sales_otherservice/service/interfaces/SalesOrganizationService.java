package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface SalesOrganizationService {
	SalesOrganizationResponse saveSo(SalesOrganizationRequest salesOrganizationRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	SalesOrganizationResponse getSoById(Long id) throws ResourceNotFoundException;

	List<SalesOrganizationResponse> getAllSo();

	List<SalesOrganizationResponse> findAllStatusTrue();

	SalesOrganizationResponse updateSo(Long id, SalesOrganizationRequest updateSalesOrganizationRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	SalesOrganizationResponse updateSoStatus(Long id) throws ResourceNotFoundException;

	List<SalesOrganizationResponse> updateBatchSoStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteSoById(Long id) throws ResourceNotFoundException;

	void deleteBatchSo(List<Long> ids) throws ResourceNotFoundException;

}
