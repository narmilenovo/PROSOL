package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.DistributionChannelRequest;
import com.example.sales_otherservice.dto.response.DistributionChannelResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface DistributionChannelService {
	DistributionChannelResponse saveDc(DistributionChannelRequest deliveringPlantRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	DistributionChannelResponse getDcById(@NonNull Long id) throws ResourceNotFoundException;

	List<DistributionChannelResponse> getAllDc();

	List<DistributionChannelResponse> findAllStatusTrue();

	DistributionChannelResponse updateDc(@NonNull Long id, DistributionChannelRequest updateDistributionChannelRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	DistributionChannelResponse updateDcStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<DistributionChannelResponse> updateBatchDcStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deleteDcId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchDc(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
