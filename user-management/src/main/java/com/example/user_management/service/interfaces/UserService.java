package com.example.user_management.service.interfaces;

import com.example.user_management.dto.request.UpdatePasswordRequest;
import com.example.user_management.dto.request.UpdateUserRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.dto.request.UserRoleRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.Role;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Set;

public interface UserService {
    UserResponse saveUser(UserRequest userRequest) throws ResourceFoundException;

    List<UserResponse> saveAllUser(List<UserRequest> userRequests);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id) throws ResourceNotFoundException;

    void deleteUserId(Long id) throws ResourceNotFoundException;

    void deleteBatch(List<Long> id);

    UserResponse updateUser(Long id, UpdateUserRequest updateUserRequest) throws ResourceNotFoundException;

    UserResponse updateStatusUsingId(Long id) throws ResourceNotFoundException;

    List<UserResponse> updateBulkStatusUsingId(List<Long> id);

    UserResponse updatePassword(Long id, UpdatePasswordRequest updatePasswordRequest) throws ResourceNotFoundException;

    void updatePassword(Long id, String newPassword) throws ResourceNotFoundException;

    UserResponse findByEmail(String email) throws ResourceNotFoundException;

    Set<Role> setToString(String[] roles);

    UserResponse addRolesToUser(Long id, UserRoleRequest userRoleRequest) throws ResourceNotFoundException;

    UserResponse removeRolesFromUser(Long id, UserRoleRequest userRoleRequest) throws ResourceNotFoundException;
}
