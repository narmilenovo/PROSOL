package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface PurchasingGroupService {
    PurchasingGroupResponse savePg(PurchasingGroupRequest purchasingGroupRequest) throws ResourceFoundException;

    List<PurchasingGroupResponse> getAllPg();

    PurchasingGroupResponse getPgById(Long id) throws ResourceNotFoundException;

    List<PurchasingGroupResponse> findAllStatusTrue();

    PurchasingGroupResponse updatePg(Long id, PurchasingGroupRequest updatePurchasingGroupRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deletePgById(Long id) throws ResourceNotFoundException;
}
