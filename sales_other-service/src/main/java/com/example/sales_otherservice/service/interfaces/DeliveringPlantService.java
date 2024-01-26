package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.clients.DpPlant;
import com.example.sales_otherservice.dto.request.DeliveringPlantRequest;
import com.example.sales_otherservice.dto.response.DeliveringPlantResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface DeliveringPlantService {
	DeliveringPlantResponse saveDp(DeliveringPlantRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	DeliveringPlantResponse getDpById(Long id) throws ResourceNotFoundException;

	DpPlant getDpPlantById(Long id) throws ResourceNotFoundException;

	List<DeliveringPlantResponse> getAllDp();

	List<DpPlant> getAllDpPlant();

	List<DeliveringPlantResponse> findAllStatusTrue();

	DeliveringPlantResponse updateDp(Long id, DeliveringPlantRequest updateDeliveringPlantRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	DeliveringPlantResponse updateDpStatus(Long id) throws ResourceNotFoundException;

	List<DeliveringPlantResponse> updateBatchDpStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteDpId(Long id) throws ResourceNotFoundException;

	void deleteBatchDp(List<Long> ids) throws ResourceNotFoundException;
}
