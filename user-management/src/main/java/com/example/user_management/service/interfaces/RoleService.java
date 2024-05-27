package com.example.user_management.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.user_management.client.RolePlantResponse;
import com.example.user_management.dto.request.RolePrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.Privilege;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;

public interface RoleService {
	RoleResponse saveRole(RoleRequest roleRequest) throws ResourceFoundException, ResourceNotFoundException;

	RoleResponse getRoleById(@NonNull Long id) throws ResourceNotFoundException;

	RolePlantResponse getRolePlantById(@NonNull Long id) throws ResourceNotFoundException;

	List<RoleResponse> getAllRoles();

	List<RolePlantResponse> getAllRolesPlant();

	List<RoleResponse> findAllStatusTrue();

	List<RolePlantResponse> findAllRolesPlantStatusTrue();

	List<RoleResponse> findAllByPlantId(Long plantId);

	List<RolePlantResponse> findAllRolesPlantByPlantId(Long plantId);

	RoleResponse updateRole(@NonNull Long id, RoleRequest updateRoleRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	List<RoleResponse> updateBulkStatusRoleId(@NonNull List<Long> id) throws ResourceNotFoundException;

	RoleResponse updateStatusUsingRoleId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteRoleId(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchRole(@NonNull List<Long> id) throws ResourceNotFoundException;

	List<Privilege> setToPrivilegeId(Long[] privileges);

	RoleResponse unassignPrivilegesFromRole(@NonNull Long id, RolePrivilegeRequest rolePrivilegeRequest)
			throws ResourceNotFoundException;

	RoleResponse assignPrivilegesToRole(@NonNull Long id, RolePrivilegeRequest updateRolePrivilegeRequest)
			throws ResourceNotFoundException;

}
