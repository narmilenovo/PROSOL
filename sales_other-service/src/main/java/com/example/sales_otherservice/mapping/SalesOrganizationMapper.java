package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;

import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.entity.SalesOrganization;

@Mapper(componentModel = "spring")
public interface SalesOrganizationMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	SalesOrganization mapToSalesOrganization(SalesOrganizationRequest salesOrganizationRequest);

	SalesOrganizationResponse mapToSalesOrganizationResponse(SalesOrganization salesOrganization);
}
