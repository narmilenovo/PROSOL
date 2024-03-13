package com.example.sales_otherservice.mapping;

import org.mapstruct.Mapper;

import com.example.sales_otherservice.dto.request.ItemCategoryGroupRequest;
import com.example.sales_otherservice.dto.response.ItemCategoryGroupResponse;
import com.example.sales_otherservice.entity.ItemCategoryGroup;

@Mapper(componentModel = "spring")
public interface ItemCategoryGroupMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	ItemCategoryGroup mapToItemCategoryGroup(ItemCategoryGroupRequest categoryGroupRequest);

	ItemCategoryGroupResponse mapToItemCategoryGroupResponse(ItemCategoryGroup itemCategoryGroup);
}
