package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.DistributionChannelRequest;
import com.example.sales_otherservice.dto.response.DistributionChannelResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface DistributionChannelService {
    DistributionChannelResponse saveDc(DistributionChannelRequest deliveringPlantRequest) throws ResourceFoundException, ResourceNotFoundException;

    List<DistributionChannelResponse> getAllDc();

    DistributionChannelResponse getDcById(Long id) throws ResourceNotFoundException;

    List<DistributionChannelResponse> findAllStatusTrue();

    DistributionChannelResponse updateDc(Long id, DistributionChannelRequest updateDistributionChannelRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteDcId(Long id) throws ResourceNotFoundException;
}
