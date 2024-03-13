package com.example.user_management.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.user_management.client.UserDepartmentPlantResponse;
import com.example.user_management.client.UserDepartmentResponse;
import com.example.user_management.client.UserPlantResponse;
import com.example.user_management.dto.request.TokenRequest;
import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.Token;
import com.example.user_management.entity.User;
import com.example.user_management.entity.UserAccount;

@Mapper(componentModel = "spring")
public interface UserMapper {

//	@Mapping(target = "id", ignore = true)
	@Mapping(target = "roles", source = "roles", ignore = true)
//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	User mapToUser(UserRequest userRequest);

	UserResponse mapToUserResponse(User user);

//	@Mapping(target = "department", ignore = true)
	UserDepartmentResponse mapToUserDepartmentResponse(User user);

//	@Mapping(target = "plants", ignore = true)
	UserPlantResponse mapToUserPlantResponse(User user);

//	@Mapping(target = "department", ignore = true)
//	@Mapping(target = "plants", ignore = true)
	UserDepartmentPlantResponse mapToUserDepartmentPlantResponse(User user);

//	@Mapping(target = "password", ignore = true)
//	@Mapping(target = "roles", ignore = true)
	User mapUserResponseToUser(UserResponse userResponse);

//	@Mapping(target = "id", ignore = true)
	Token mapToToken(TokenRequest tokenRequest);

//	@Mapping(target = "createdAt", ignore = true)
//	@Mapping(target = "createdBy", ignore = true)
//	@Mapping(target = "updateAuditHistories", ignore = true)
	UserAccount mapToUserAccount(UserAccountRequest userAccountRequest);

	UserAccountRequest mapToUserAccountRequest(UserAccount userAccount);

}
