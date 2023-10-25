package com.example.vendor_masterservice.service.interfaces;

import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface VendorMasterService {
    VendorMasterResponse saveVm(VendorMasterRequest vendorMasterRequest);

    List<VendorMasterResponse> saveAllVm(List<VendorMasterRequest> vendorMasterRequests);

    List<VendorMasterResponse> getAllVm();

    VendorMasterResponse getVmById(Long id) throws ResourceNotFoundException;

    List<VendorMasterResponse> findAllStatusTrue();

    VendorMasterResponse updateVm(Long id, VendorMasterRequest updateVendorMasterRequest) throws ResourceNotFoundException;

    void deleteVmId(Long id) throws ResourceNotFoundException;

}
