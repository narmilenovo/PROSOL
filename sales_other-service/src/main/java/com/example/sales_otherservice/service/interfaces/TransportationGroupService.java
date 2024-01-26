package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface TransportationGroupService {
	TransportationGroupResponse saveTg(TransportationGroupRequest transportationGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	TransportationGroupResponse getTgById(Long id) throws ResourceNotFoundException;

	List<TransportationGroupResponse> getAllTg();

	List<TransportationGroupResponse> findAllStatusTrue();

	TransportationGroupResponse updateTg(Long id, TransportationGroupRequest updateTransportationGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	TransportationGroupResponse updateTgStatus(Long id) throws ResourceNotFoundException;

	List<TransportationGroupResponse> updateBatchTgStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteTgById(Long id) throws ResourceNotFoundException;

	void deleteBatchTg(List<Long> ids) throws ResourceNotFoundException;

}
