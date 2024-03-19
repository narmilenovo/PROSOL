package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.entity.PurchasingGroup;

@Mapper(componentModel = "spring")
public interface PurchasingGroupMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	PurchasingGroup mapToPurchasingGroup(PurchasingGroupRequest purchasingGroupRequest);

	PurchasingGroupResponse mapToPurchasingGroupResponse(PurchasingGroup purchasingGroup);

}
