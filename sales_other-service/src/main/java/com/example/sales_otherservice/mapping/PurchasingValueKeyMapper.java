package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.entity.PurchasingValueKey;

@Mapper(componentModel = "spring")
public interface PurchasingValueKeyMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	PurchasingValueKey mapToPurchasingValueKey(PurchasingValueKeyRequest purchasingValueKeyRequest);

	PurchasingValueKeyResponse mapToPurchasingValueKeyResponse(PurchasingValueKey purchasingValueKey);
}
