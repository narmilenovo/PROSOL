package com.example.plantservice.mapping;

import org.mapstruct.Mapper;

import com.example.plantservice.dto.request.StorageBinRequest;
import com.example.plantservice.dto.response.StorageBinResponse;
import com.example.plantservice.entity.StorageBin;

@Mapper(componentModel = "spring")
public interface StorageBinMapper {

//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
//	@Mapping(target = "plant", ignore = true)
//	@Mapping(target = "storageLocation", ignore = true)
	StorageBin mapToStorageBin(StorageBinRequest storageBinRequest);

	StorageBinResponse mapToStorageBinResponse(StorageBin storageLocation);
}
