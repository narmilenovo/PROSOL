package com.example.user_management.service.interfaces;

import com.example.user_management.dto.request.RolePrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.Privilege;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Set;

public interface RoleService {
    RoleResponse saveRole(RoleRequest roleRequest) throws ResourceFoundException;

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id) throws ResourceNotFoundException;

    RoleResponse updateRole(Long id, RoleRequest updateRoleRequest) throws ResourceNotFoundException, ResourceFoundException;

    List<RoleResponse> updateBulkStatusRoleId(List<Long> id);

    RoleResponse updateStatusUsingRoleId(Long id) throws ResourceNotFoundException;

    void deleteRoleId(Long id) throws ResourceNotFoundException;

    void deleteBatchRole(List<Long> id);

    List<RoleResponse> findAllStatusTrue();

    Set<Privilege> setToString(String[] privileges);

    RoleResponse removePrivilegesFromRole(Long id, RolePrivilegeRequest rolePrivilegeRequest) throws ResourceNotFoundException;

    RoleResponse addPrivilegesToRole(Long id, RolePrivilegeRequest updateRolePrivilegeRequest) throws ResourceNotFoundException;
}
