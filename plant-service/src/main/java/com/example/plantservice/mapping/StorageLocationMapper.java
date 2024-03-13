package com.example.plantservice.mapping;

import org.mapstruct.Mapper;

import com.example.plantservice.dto.request.StorageLocationRequest;
import com.example.plantservice.dto.response.StorageLocationResponse;
import com.example.plantservice.entity.StorageLocation;

@Mapper(componentModel = "spring")
public interface StorageLocationMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
//	@Mapping(target = "plant", ignore = true)
	StorageLocation mapToStorageLocation(StorageLocationRequest storageLocationRequest);

	StorageLocationResponse mapToStorageLocationResponse(StorageLocation storageLocation);
}
