package com.example.mrpdataservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mrpdataservice.entity.LotSize;
import com.example.mrpdataservice.request.LotSizeRequest;
import com.example.mrpdataservice.response.LotSizeResponse;

@Mapper(componentModel = "spring")
public interface LotSizeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	LotSize mapToLotSize(LotSizeRequest lotSizeRequest);

	LotSizeResponse mapToLotSizeResponse(LotSize lotSize);
}
