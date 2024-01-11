package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface TaxClassificationClassService {
	TaxClassificationClassResponse saveTcc(TaxClassificationClassRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<TaxClassificationClassResponse> getAllTcc();

	TaxClassificationClassResponse getTccById(Long id) throws ResourceNotFoundException;

	List<TaxClassificationClassResponse> findAllStatusTrue();

	TaxClassificationClassResponse updateTcc(Long id, TaxClassificationClassRequest updateTaxClassificationClassRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteTccById(Long id) throws ResourceNotFoundException;

	void deleteBatchTcc(List<Long> ids);
}
