package com.example.attributemaster.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.attributemaster.client.AttributeMasterUomResponse;
import com.example.attributemaster.entity.AttributeMaster;
import com.example.attributemaster.request.AttributeMasterRequest;
import com.example.attributemaster.response.AttributeMasterResponse;

@Mapper(componentModel = "spring")
public interface AttributeMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	AttributeMaster mapToAttributeMaster(AttributeMasterRequest attributeMasterRequest);

	AttributeMasterResponse mapToAttributeMasterResponse(AttributeMaster attributeMaster);

	@Mapping(target = "listUom", ignore = true)
	AttributeMasterUomResponse mapToAttributeMasterUomResponse(AttributeMaster attributeMaster);
}
