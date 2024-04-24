package com.example.generalsettings.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.generalsettings.entity.Hsn;
import com.example.generalsettings.request.HsnRequest;
import com.example.generalsettings.response.HsnResponse;

@Mapper(componentModel = "spring")
public interface HsnMap {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	Hsn mapToHsn(HsnRequest hsnRequest);

	HsnResponse mapToHsnResponse(Hsn hsn);
}
