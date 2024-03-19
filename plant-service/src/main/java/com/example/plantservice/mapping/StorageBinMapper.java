package com.example.plantservice.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.plantservice.dto.request.StorageBinRequest;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.entity.StorageBin;

@Mapper(componentModel = "spring")
public interface StorageBinMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "plant.id", source = "plantId")
	@Mapping(target = "storageLocation.id", source = "storageLocationId")
	StorageBin mapToStorageBin(StorageBinRequest storageBinRequest);

	StorageBinResponse mapToStorageBinResponse(StorageBin storageLocation);
}
