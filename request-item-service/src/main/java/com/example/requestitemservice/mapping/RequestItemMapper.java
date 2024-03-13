package com.example.requestitemservice.mapping;

import org.mapstruct.Mapper;

import com.example.requestitemservice.client.MaterialItem;
import com.example.requestitemservice.dto.request.RequestItemRequest;
import com.example.requestitemservice.dto.response.RequestItemResponse;
import com.example.requestitemservice.entity.RequestItem;

@Mapper(componentModel = "spring")
public interface RequestItemMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	RequestItem mapToRequestItem(RequestItemRequest requestItemRequest);

	RequestItemResponse mapToRequestItemResponse(RequestItem requestItem);

//	@Mapping(target = "industrySector", ignore = true)
//	@Mapping(target = "materialGroup", ignore = true)
//	@Mapping(target = "materialType", ignore = true)
//	@Mapping(target = "plant", ignore = true)
//	@Mapping(target = "storageLocation", ignore = true)
	MaterialItem mapToMaterialItem(RequestItem requestItem);

}
