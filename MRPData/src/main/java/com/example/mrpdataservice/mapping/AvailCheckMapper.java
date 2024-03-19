package com.example.mrpdataservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.mrpdataservice.entity.AvailCheck;
import com.example.mrpdataservice.request.AvailCheckRequest;
import com.example.mrpdataservice.response.AvailCheckResponse;

@Mapper(componentModel = "spring")
public interface AvailCheckMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	AvailCheck mapToAvailCheck(AvailCheckRequest availCheckRequest);

	AvailCheckResponse mapToAvailCheckResponse(AvailCheck availCheck);

}
