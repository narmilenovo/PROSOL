package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface TransportationGroupService {
    TransportationGroupResponse saveTg(TransportationGroupRequest transportationGroupRequest) throws ResourceFoundException;

    List<TransportationGroupResponse> getAllTg();

    TransportationGroupResponse getTgById(Long id) throws ResourceNotFoundException;

    List<TransportationGroupResponse> findAllStatusTrue();

    TransportationGroupResponse updateTg(Long id, TransportationGroupRequest updateTransportationGroupRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteTgById(Long id) throws ResourceNotFoundException;

}
