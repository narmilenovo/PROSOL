package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.TaxClassificationTypeRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationTypeResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface TaxClassificationTypeService {
	TaxClassificationTypeResponse saveTct(TaxClassificationTypeRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	TaxClassificationTypeResponse getTctById(Long id) throws ResourceNotFoundException;

	List<TaxClassificationTypeResponse> getAllTct();

	List<TaxClassificationTypeResponse> findAllStatusTrue();

	TaxClassificationTypeResponse updateTct(Long id, TaxClassificationTypeRequest updateTaxClassificationTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	TaxClassificationTypeResponse updateTctStatus(Long id) throws ResourceNotFoundException;

	List<TaxClassificationTypeResponse> updateBatchTctStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteTctById(Long id) throws ResourceNotFoundException;

	void deleteBatchTct(List<Long> ids) throws ResourceNotFoundException;
}
