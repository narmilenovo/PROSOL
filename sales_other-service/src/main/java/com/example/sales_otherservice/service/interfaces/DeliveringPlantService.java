package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.clients.DpPlant;
import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface DeliveringPlantService {
	DeliveringPlantResponse saveDp(DeliveringPlantRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	DeliveringPlantResponse getDpById(@NonNull Long id) throws ResourceNotFoundException;

	DpPlant getDpPlantById(@NonNull Long id) throws ResourceNotFoundException;

	List<DeliveringPlantResponse> getAllDp();

	List<DpPlant> getAllDpPlant();

	List<DeliveringPlantResponse> findAllStatusTrue();

	DeliveringPlantResponse updateDp(@NonNull Long id, DeliveringPlantRequest updateDeliveringPlantRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	DeliveringPlantResponse updateDpStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<DeliveringPlantResponse> updateBatchDpStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteDpId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchDp(@NonNull List<Long> ids) throws ResourceNotFoundException;
}
