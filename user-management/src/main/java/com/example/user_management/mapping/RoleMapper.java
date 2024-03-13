package com.example.user_management.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.user_management.client.RolePlantResponse;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
//	@Mapping(target = "id", ignore = true)
//	@Mapping(target = "users", ignore = true)
	@Mapping(target = "privileges", source = "privileges", ignore = true)
	Role mapToRole(RoleRequest roleRequest);

	RoleResponse mapToRoleResponse(Role role);

//	@Mapping(target = "plant", ignore = true)
	RolePlantResponse mapToRolePlantResponse(Role role);
}
