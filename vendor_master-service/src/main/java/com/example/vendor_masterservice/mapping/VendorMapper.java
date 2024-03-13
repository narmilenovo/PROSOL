package com.example.vendor_masterservice.mapping;

import org.mapstruct.Mapper;

import com.example.vendor_masterservice.dto.request.VendorMasterRequest;
import com.example.vendor_masterservice.dto.response.VendorMasterResponse;
import com.example.vendor_masterservice.entity.VendorMaster;

@Mapper(componentModel = "spring")
public interface VendorMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	VendorMaster mapToVendorMaster(VendorMasterRequest vendorMasterRequest);

	VendorMasterResponse mapToVendorMasterResponse(VendorMaster vendorMaster);
}
