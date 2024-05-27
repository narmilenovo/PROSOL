package com.example.user_management.mapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.user_management.client.RolePlantResponse;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.Privilege;
import com.example.user_management.entity.Role;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,componentModel = "spring", uses = PrivilegeMapper.class)
public interface RoleMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "assignee", ignore = true)
	@Mapping(target = "privileges", source = "privileges", qualifiedByName = "mapToPrivileges")
	@Mapping(target = "subRole.id", source = "subRole")
	Role mapToRole(RoleRequest roleRequest);

	@Mapping(target = "subRole", source = "subRole.name")
	RoleResponse mapToRoleResponse(Role role);

	@Mapping(target = "plant.id", source = "plantId")
	RolePlantResponse mapToRolePlantResponse(Role role);

	@Mapping(target = "assignee", ignore = true)
	@Mapping(target = "privileges", source = "privileges")
	@Mapping(target = "subRole.name", source = "subRole")
	Role mapRoleResponseToRole(RoleResponse roleResponse);

	@Named("mapToPrivileges")
	default List<Privilege> mapToPrivileges(Long[] privileges) {
		if (privileges == null || privileges.length == 0) {
			return Collections.emptyList();
		}
		return Arrays.stream(privileges).map(privilegeId -> {
			Privilege privilege = new Privilege();
			privilege.setId(privilegeId);
			return privilege;
		}).toList();
	}
}
