package com.example.attributemaster.mapping;

import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.attributemaster.client.AttributeMasterUomResponse;
import com.example.attributemaster.client.GeneralSettings.AttributeUomResponse;
import com.example.attributemaster.entity.AttributeMaster;
import com.example.attributemaster.request.AttributeMasterRequest;
import com.example.attributemaster.response.AttributeMasterResponse;

@Mapper(componentModel = "spring")
public interface AttributeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	AttributeMaster mapToAttributeMaster(AttributeMasterRequest attributeMasterRequest);

	AttributeMasterResponse mapToAttributeMasterResponse(AttributeMaster attributeMaster);

	@Mapping(target = "listUom", source = "listUom", qualifiedByName = "mapListUom")
	AttributeMasterUomResponse mapToAttributeMasterUomResponse(AttributeMaster attributeMaster);

	@Named("mapListUom")
	default List<AttributeUomResponse> mapListUom(List<Long> listUomIds) {
		if (listUomIds == null || listUomIds.isEmpty()) {
			return Collections.emptyList();
		}
		return listUomIds.stream().map(id -> new AttributeUomResponse()).toList();
	}
}
