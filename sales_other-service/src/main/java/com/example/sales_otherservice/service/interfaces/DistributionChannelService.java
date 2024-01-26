package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.DistributionChannelRequest;
import com.example.sales_otherservice.dto.response.DistributionChannelResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface DistributionChannelService {
	DistributionChannelResponse saveDc(DistributionChannelRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	DistributionChannelResponse getDcById(Long id) throws ResourceNotFoundException;

	List<DistributionChannelResponse> getAllDc();

	List<DistributionChannelResponse> findAllStatusTrue();

	DistributionChannelResponse updateDc(Long id, DistributionChannelRequest updateDistributionChannelRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	DistributionChannelResponse updateDcStatus(Long id) throws ResourceNotFoundException;

	List<DistributionChannelResponse> updateBatchDcStatus(List<Long> ids) throws ResourceNotFoundException;

	void deleteDcId(Long id) throws ResourceNotFoundException;

	void deleteBatchDc(List<Long> ids) throws ResourceNotFoundException;

}
