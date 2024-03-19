package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.entity.LoadingGroup;

@Mapper(componentModel = "spring")
public interface LoadingGroupMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	LoadingGroup mapToLoadingGroup(LoadingGroupRequest loadingGroupRequest);

	LoadingGroupResponse mapToLoadingGroupResponse(LoadingGroup loadingGroup);
}
