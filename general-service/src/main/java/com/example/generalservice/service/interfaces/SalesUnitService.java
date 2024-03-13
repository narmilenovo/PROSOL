package com.example.generalservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.generalservice.dto.request.SalesUnitRequest;
import com.example.generalservice.dto.response.SalesUnitResponse;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;

public interface SalesUnitService {
	SalesUnitResponse saveSalesUnit(SalesUnitRequest salesUnitRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	SalesUnitResponse getSalesUnitById(@NonNull Long id) throws ResourceNotFoundException;

	List<SalesUnitResponse> getAllSalesUnit();

	List<SalesUnitResponse> findAllStatusTrue();

	SalesUnitResponse updateSalesUnit(@NonNull Long id, SalesUnitRequest updateSalesUnitRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	SalesUnitResponse updateSalesUnitStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<SalesUnitResponse> updateBatchSalesUnitStatus(@NonNull List<Long> ids);

	void deleteSalesUnitId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchSalesUnit(List<Long> ids) throws ResourceNotFoundException;

}
