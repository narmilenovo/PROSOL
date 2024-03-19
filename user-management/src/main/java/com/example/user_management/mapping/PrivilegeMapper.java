package com.example.user_management.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.response.PrivilegeResponse;
import com.example.user_management.entity.Privilege;

@Mapper(componentModel = "spring")
public interface PrivilegeMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "roles", ignore = true)
	Privilege mapToPrivilege(PrivilegeRequest privilegeRequest);

	PrivilegeResponse mapToPrivilegeResponse(Privilege privilege);
}
