package com.example.vendor_masterservice.service.interfaces;

import java.util.List;

import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;

public interface VendorMasterService {
    VendorMasterResponse saveVm(VendorMasterRequest vendorMasterRequest) throws ResourceNotFoundException;

    List<VendorMasterResponse> saveAllVm(List<VendorMasterRequest> vendorMasterRequests);

    VendorMasterResponse getVmById(Long id) throws ResourceNotFoundException;

    List<VendorMasterResponse> getAllVm();

    List<VendorMasterResponse> findAllStatusTrue();

    VendorMasterResponse updateVm(Long id, VendorMasterRequest updateVendorMasterRequest)
            throws ResourceNotFoundException;

    void deleteVmId(Long id) throws ResourceNotFoundException;

}
