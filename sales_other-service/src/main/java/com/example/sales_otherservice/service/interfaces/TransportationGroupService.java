package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface TransportationGroupService {
	TransportationGroupResponse saveTg(TransportationGroupRequest transportationGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	TransportationGroupResponse getTgById(@NonNull Long id) throws ResourceNotFoundException;

	List<TransportationGroupResponse> getAllTg();

	List<TransportationGroupResponse> findAllStatusTrue();

	TransportationGroupResponse updateTg(@NonNull Long id, TransportationGroupRequest updateTransportationGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	TransportationGroupResponse updateTgStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<TransportationGroupResponse> updateBatchTgStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteTgById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchTg(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
