package com.example.plantservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.StorageLocation;

@Mapper(componentModel = "spring")
public interface StorageLocationMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "plant.id", source = "plantId")
	StorageLocation mapToStorageLocation(StorageLocationRequest storageLocationRequest);

	StorageLocationResponse mapToStorageLocationResponse(StorageLocation storageLocation);
}
