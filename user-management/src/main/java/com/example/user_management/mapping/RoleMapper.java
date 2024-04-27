package com.example.user_management.mapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.user_management.client.RolePlantResponse;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.Privilege;
import com.example.user_management.entity.Role;

@Mapper(componentModel = "spring", uses = { UserMapper.class, PrivilegeMapper.class })
public interface RoleMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "users", ignore = true)
	@Mapping(target = "privileges", source = "privileges", qualifiedByName = "mapToPrivileges")
	Role mapToRole(RoleRequest roleRequest);

	RoleResponse mapToRoleResponse(Role role);

	@Mapping(target = "plant.id", source = "plantId")
	RolePlantResponse mapToRolePlantResponse(Role role);

	@Named("mapToPrivileges")
	default List<Privilege> mapToPrivileges(Long[] privileges) {
		if (privileges == null || privileges.length == 0) {
			return Collections.emptyList();
		}
		return Arrays.stream(privileges).map(privilegeId -> {
			Privilege privilege = new Privilege();
			privilege.setId(privilegeId);
			return privilege;
		}).collect(Collectors.toList());
	}

	@Mapping(target = "users", ignore = true)
	@Mapping(target = "privileges", source = "privileges")
	Role mapRoleResponseToRole(RoleResponse roleResponse);
}
