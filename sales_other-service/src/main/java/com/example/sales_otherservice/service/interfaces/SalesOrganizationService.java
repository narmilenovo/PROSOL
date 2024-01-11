package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface SalesOrganizationService {
	SalesOrganizationResponse saveSo(SalesOrganizationRequest salesOrganizationRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<SalesOrganizationResponse> getAllSo();

	SalesOrganizationResponse getSoById(Long id) throws ResourceNotFoundException;

	List<SalesOrganizationResponse> findAllStatusTrue();

	SalesOrganizationResponse updateSo(Long id, SalesOrganizationRequest updateSalesOrganizationRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteSoById(Long id) throws ResourceNotFoundException;

	void deleteBatchSo(List<Long> ids);
}
