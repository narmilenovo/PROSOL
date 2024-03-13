package com.example.user_management.service.interfaces;

import java.util.List;
import java.util.Set;

import org.springframework.lang.NonNull;

import com.example.user_management.client.UserDepartmentPlantResponse;
import com.example.user_management.client.UserDepartmentResponse;
import com.example.user_management.client.UserPlantResponse;
import com.example.user_management.dto.request.UpdatePasswordRequest;
import com.example.user_management.dto.request.UpdateUserRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.dto.request.UserRoleRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.Role;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;

public interface UserService {
	UserResponse saveUser(UserRequest userRequest) throws ResourceFoundException, ResourceNotFoundException;

	List<UserResponse> saveAllUser(List<UserRequest> userRequests);

	UserResponse getUserById(@NonNull Long id, String show) throws ResourceNotFoundException;

	UserPlantResponse getUserPlantById(@NonNull Long id, String show) throws ResourceNotFoundException;

	UserDepartmentResponse getUserDepartmentById(@NonNull Long id, String show) throws ResourceNotFoundException;

	UserDepartmentPlantResponse getUserDepartmentPlantById(@NonNull Long id, String show)
			throws ResourceNotFoundException;

	UserResponse findByEmail(String email) throws ResourceNotFoundException;

	List<UserResponse> getAllUsers(String show);

	List<UserPlantResponse> getAllUserPlants(String show);

	List<UserDepartmentResponse> getAllUserDepartment(String show);

	List<UserDepartmentPlantResponse> getAllUserDepartmentPlants(String show);

	List<UserResponse> getAllUsersByPlantId(String show, List<Long> plantIds);

	List<UserDepartmentResponse> getAllUserDepartmentByPlantId(String show, List<Long> plantIds);

	List<UserDepartmentPlantResponse> getAllUserDepartmentPlantsByPlantId(String show, List<Long> plantIds);

	List<UserPlantResponse> getAllUserPlantsByPlantId(String show, List<Long> plantIds);

	UserResponse updateUser(@NonNull Long id, UpdateUserRequest updateUserRequest) throws ResourceNotFoundException;

	UserResponse updateStatusUsingId(@NonNull Long id) throws ResourceNotFoundException;

	List<UserResponse> updateBulkStatusUsingId(@NonNull List<Long> id) throws ResourceNotFoundException;

	UserResponse updatePassword(@NonNull Long id, UpdatePasswordRequest updatePasswordRequest)
			throws ResourceNotFoundException;

	void updatePassword(@NonNull Long id, String newPassword) throws ResourceNotFoundException;

	void deleteUserId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatch(@NonNull List<Long> id) throws ResourceNotFoundException;

	Set<Role> setToRoleId(Long[] roles);

	UserResponse addRolesToUser(@NonNull Long id, UserRoleRequest userRoleRequest) throws ResourceNotFoundException;

	UserResponse removeRolesFromUser(@NonNull Long id, UserRoleRequest userRoleRequest)
			throws ResourceNotFoundException;

}
