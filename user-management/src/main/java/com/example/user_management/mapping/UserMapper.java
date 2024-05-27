package com.example.user_management.mapping;

import java.util.Collections;
import java.util.List;

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
import com.example.user_management.entity.Token;
import com.example.user_management.entity.User;
import com.example.user_management.entity.UserAccount;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,componentModel = "spring", uses = AssigneeMapper.class)
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "createdBy", ignore = true)
	@Mapping(target = "avatar", ignore = true)
	@Mapping(target = "updateAuditHistories", ignore = true)
	User mapToUser(UserRequest userRequest);

	UserResponse mapToUserResponse(User user);

	@Mapping(target = "department.id", source = "departmentId")
	UserDepartmentResponse mapToUserDepartmentResponse(User user);

	@Mapping(target = "plants", source = "plantId", qualifiedByName = "mapToPlants")
	UserPlantResponse mapToUserPlantResponse(User user);

	@Mapping(target = "department.id", source = "departmentId")
	@Mapping(target = "plants", source = "plantId", qualifiedByName = "mapToPlants")
	UserDepartmentPlantResponse mapToUserDepartmentPlantResponse(User user);

	@Mapping(target = "password", ignore = true)
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


	@Named("mapToPlants")
	default List<PlantResponse> mapToPlants(List<Long> plantIds) {
		if (plantIds == null || plantIds.isEmpty()) {
			return Collections.emptyList();
		}
		return plantIds.stream().map(plantId -> new PlantResponse()).toList();
	}
}
