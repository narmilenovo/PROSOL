package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface TaxClassificationClassService {
	TaxClassificationClassResponse saveTcc(TaxClassificationClassRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	TaxClassificationClassResponse getTccById(Long id) throws ResourceNotFoundException;

	List<TaxClassificationClassResponse> getAllTcc();

	List<TaxClassificationClassResponse> findAllStatusTrue();

	TaxClassificationClassResponse updateTcc(Long id, TaxClassificationClassRequest updateTaxClassificationClassRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	TaxClassificationClassResponse updateTccStatus(Long id) throws ResourceNotFoundException;

	List<TaxClassificationClassResponse> updateBatchTccStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteTccById(Long id) throws ResourceNotFoundException;

	void deleteBatchTcc(List<Long> ids) throws ResourceNotFoundException;
}
