package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface TaxClassificationClassService {
	TaxClassificationClassResponse saveTcc(TaxClassificationClassRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	TaxClassificationClassResponse getTccById(@NonNull Long id) throws ResourceNotFoundException;

	List<TaxClassificationClassResponse> getAllTcc();

	List<TaxClassificationClassResponse> findAllStatusTrue();

	TaxClassificationClassResponse updateTcc(@NonNull Long id,
			TaxClassificationClassRequest updateTaxClassificationClassRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	TaxClassificationClassResponse updateTccStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<TaxClassificationClassResponse> updateBatchTccStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteTccById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchTcc(@NonNull List<Long> ids) throws ResourceNotFoundException;
}
