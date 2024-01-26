package com.example.user_management.service.interfaces;

import java.util.List;
import java.util.Set;

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
    UserResponse saveUser(UserRequest userRequest) throws ResourceFoundException;

    List<UserResponse> saveAllUser(List<UserRequest> userRequests);

    UserResponse getUserById(Long id, String show) throws ResourceNotFoundException;

    UserPlantResponse getUserPlantById(Long id, String show) throws ResourceNotFoundException;

    UserDepartmentResponse getUserDepartmentById(Long id, String show) throws ResourceNotFoundException;

    UserDepartmentPlantResponse getUserDepartmentPlantById(Long id, String show) throws ResourceNotFoundException;

    UserResponse findByEmail(String email) throws ResourceNotFoundException;

    List<UserResponse> getAllUsers(String show);

    List<UserPlantResponse> getAllUserPlants(String show);

    List<UserDepartmentResponse> getAllUserDepartment(String show);

    List<UserDepartmentPlantResponse> getAllUserDepartmentPlants(String show);

    UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) throws ResourceNotFoundException;

    UserResponse updateStatusUsingId(Long id) throws ResourceNotFoundException;

    List<UserResponse> updateBulkStatusUsingId(List<Long> id) throws ResourceNotFoundException;

    UserResponse updatePassword(Long id, UpdatePasswordRequest updatePasswordRequest) throws ResourceNotFoundException;

    void updatePassword(Long id, String newPassword) throws ResourceNotFoundException;

    void deleteUserId(Long id) throws ResourceNotFoundException;

    void deleteBatch(List<Long> id) throws ResourceNotFoundException;

    Set<Role> setToString(Long[] roles);

    UserResponse addRolesToUser(Long id, UserRoleRequest userRoleRequest) throws ResourceNotFoundException;

    UserResponse removeRolesFromUser(Long id, UserRoleRequest userRoleRequest) throws ResourceNotFoundException;

}
