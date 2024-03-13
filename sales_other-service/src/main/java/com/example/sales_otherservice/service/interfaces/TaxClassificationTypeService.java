package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.TaxClassificationTypeRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationTypeResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface TaxClassificationTypeService {
	TaxClassificationTypeResponse saveTct(TaxClassificationTypeRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	TaxClassificationTypeResponse getTctById(@NonNull Long id) throws ResourceNotFoundException;

	List<TaxClassificationTypeResponse> getAllTct();

	List<TaxClassificationTypeResponse> findAllStatusTrue();

	TaxClassificationTypeResponse updateTct(@NonNull Long id,
			TaxClassificationTypeRequest updateTaxClassificationTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	TaxClassificationTypeResponse updateTctStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<TaxClassificationTypeResponse> updateBatchTctStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteTctById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchTct(@NonNull List<Long> ids) throws ResourceNotFoundException;
}
