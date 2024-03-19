package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.dto.request.DistributionChannelRequest;
import com.example.sales_otherservice.dto.response.DistributionChannelResponse;
import com.example.sales_otherservice.entity.DistributionChannel;

@Mapper(componentModel = "spring")
public interface DistributionChannelMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "salesOrganization.id", source = "salesOrganizationId")
	DistributionChannel mapToDistributionChannel(DistributionChannelRequest distributionChannelRequest);

	DistributionChannelResponse mapToDistributionChannelResponse(DistributionChannel distributionChannel);
}
