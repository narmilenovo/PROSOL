package com.example.valueservice.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.valueservice.client.ValueAttributeUom;
import com.example.valueservice.dto.request.ValueMasterRequest;
import com.example.valueservice.dto.response.ValueMasterResponse;
import com.example.valueservice.entity.ValueMaster;

@Mapper(componentModel = "spring")
public interface ValueMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	ValueMaster mapToValueMaster(ValueMasterRequest valueMasterRequest);

	List<ValueMaster> mapToValueMasterList(List<ValueMasterRequest> valueMasterRequests);

	ValueMasterResponse mapToValueMasterResponse(ValueMaster valueMaster);

	@Mapping(target = "abbreviationUnit.id", source = "abbreviationUnit")
	@Mapping(target = "equivalentUnit.id", source = "equivalentUnit")
	ValueAttributeUom mapToValueAttributeUom(ValueMaster valueMaster);

}
