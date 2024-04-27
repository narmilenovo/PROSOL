package com.example.user_management.mapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.example.user_management.client.UserDepartmentPlantResponse;
import com.example.user_management.client.UserDepartmentResponse;
import com.example.user_management.client.UserPlantResponse;
import com.example.user_management.client.plant.PlantResponse;
import com.example.user_management.dto.request.TokenRequest;
import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.dto.response.RoleUserResponse;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.Role;
import com.example.user_management.entity.Token;
import com.example.user_management.entity.User;
import com.example.user_management.entity.UserAccount;

@Mapper(componentModel = "spring", uses = { RoleMapper.class, PrivilegeMapper.class })
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "avatar", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	@Mapping(target = "roles", source = "roles", qualifiedByName = "mapToRoles")
	User mapToUser(UserRequest userRequest);

	@Named("mapToRoles")
	default List<Role> mapToRoles(Long[] roles) {
		if (roles == null || roles.length == 0) {
			return Collections.emptyList();
		}
		return Arrays.stream(roles).map(roleId -> new Role()).collect(Collectors.toList());
	}

	UserResponse mapToUserResponse(User user);

	@Mapping(target = "department.id", source = "departmentId")
	UserDepartmentResponse mapToUserDepartmentResponse(User user);

	@Mapping(target = "plants", source = "plantId", qualifiedByName = "mapToPlants")
	UserPlantResponse mapToUserPlantResponse(User user);

	@Named("mapToPlants")
	default List<PlantResponse> mapToPlants(List<Long> plantIds) {
		if (plantIds == null || plantIds.isEmpty()) {
			return Collections.emptyList();
		}
		return plantIds.stream().map(plantId -> new PlantResponse()).toList();
	}

	@Mapping(target = "department.id", source = "departmentId")
	@Mapping(target = "plants", source = "plantId", qualifiedByName = "mapToPlants")
	UserDepartmentPlantResponse mapToUserDepartmentPlantResponse(User user);

	@Mapping(target = "password", ignore = true)
	@Mapping(target = "roles", source = "roles")
	User mapUserResponseToUser(UserResponse userResponse);

	@Mapping(target = "id", ignore = true)
	Token mapToToken(TokenRequest tokenRequest);

	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	UserAccount mapToUserAccount(UserAccountRequest userAccountRequest);

	UserAccountRequest mapToUserAccountRequest(UserAccount userAccount);

	@Mapping(target = "fullName", expression = "java(user.getFirstName() + \" \" + user.getLastName())")
	RoleUserResponse mapToRoleUserResponse(User user);

}
