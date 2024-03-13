package com.example.vendor_masterservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.exceptions.ResourceNotFoundException;

public interface VendorMasterService {
	VendorMasterResponse saveVm(VendorMasterRequest vendorMasterRequest) throws ResourceNotFoundException;

	List<VendorMasterResponse> saveAllVm(List<VendorMasterRequest> vendorMasterRequests);

	VendorMasterResponse getVmById(@NonNull Long id) throws ResourceNotFoundException;

	List<VendorMasterResponse> getAllVm();

	List<VendorMasterResponse> findAllStatusTrue();

	VendorMasterResponse updateVm(@NonNull Long id, VendorMasterRequest updateVendorMasterRequest)
			throws ResourceNotFoundException;

	VendorMasterResponse updateVmStatusById(@NonNull Long id) throws ResourceNotFoundException;

	List<VendorMasterResponse> updateBulkStatusVmId(@NonNull List<Long> id) throws ResourceNotFoundException;

	void deleteVmId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteVmBatchById(@NonNull List<Long> id) throws ResourceNotFoundException;

}
