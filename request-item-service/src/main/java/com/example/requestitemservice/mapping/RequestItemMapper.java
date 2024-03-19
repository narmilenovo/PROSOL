package com.example.requestitemservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.requestitemservice.client.MaterialItem;
import com.example.requestitemservice.dto.request.RequestItemRequest;
import com.example.requestitemservice.dto.response.RequestItemResponse;
import com.example.requestitemservice.entity.RequestItem;

@Mapper(componentModel = "spring")
public interface RequestItemMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	RequestItem mapToRequestItem(RequestItemRequest requestItemRequest);

	RequestItemResponse mapToRequestItemResponse(RequestItem requestItem);

	@Mapping(target = "plant.id", source = "plantId")
	@Mapping(target = "storageLocation.id", source = "storageLocationId")
	@Mapping(target = "materialType.id", source = "materialTypeId")
	@Mapping(target = "industrySector.id", source = "industrySectorId")
	@Mapping(target = "materialGroup.id", source = "materialGroupId")
	MaterialItem mapToMaterialItem(RequestItem requestItem);

}
