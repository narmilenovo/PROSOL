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

	List<DeliveringPlantResponse> getAllDp();

	DeliveringPlantResponse getDpById(Long id) throws ResourceNotFoundException;

	List<DeliveringPlantResponse> findAllStatusTrue();

	DeliveringPlantResponse updateDp(Long id, DeliveringPlantRequest updateDeliveringPlantRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteDpId(Long id) throws ResourceNotFoundException;

	List<DpPlant> getAllDpPlant();

	DpPlant getDpPlantById(Long id) throws ResourceNotFoundException;

	void deleteBatchDp(List<Long> ids);
}
