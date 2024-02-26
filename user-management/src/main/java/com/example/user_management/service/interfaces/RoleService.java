package com.example.user_management.service.interfaces;

import java.util.List;
import java.util.Set;

import com.example.user_management.client.RolePlantResponse;
import com.example.user_management.dto.request.RolePrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.Privilege;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;

public interface RoleService {
	RoleResponse saveRole(RoleRequest roleRequest) throws ResourceFoundException, ResourceNotFoundException;

	RoleResponse getRoleById(Long id) throws ResourceNotFoundException;

	RolePlantResponse getRolePlantById(Long id) throws ResourceNotFoundException;

	List<RoleResponse> getAllRoles();

	List<RolePlantResponse> getAllRolesPlant();

	List<RoleResponse> findAllStatusTrue();

	List<RolePlantResponse> findAllRolesPlantStatusTrue();

	List<RoleResponse> findAllByPlantId(Long plantId);

	List<RolePlantResponse> findAllRolesPlantByPlantId(Long plantId);

	RoleResponse updateRole(Long id, RoleRequest updateRoleRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	List<RoleResponse> updateBulkStatusRoleId(List<Long> id) throws ResourceNotFoundException;

	RoleResponse updateStatusUsingRoleId(Long id) throws ResourceNotFoundException;

	void deleteRoleId(Long id) throws ResourceNotFoundException;

	void deleteBatchRole(List<Long> id) throws ResourceNotFoundException;

	Set<Privilege> setToPrivilegeId(Long[] privileges);

	RoleResponse removePrivilegesFromRole(Long id, RolePrivilegeRequest rolePrivilegeRequest)
			throws ResourceNotFoundException;

	RoleResponse addPrivilegesToRole(Long id, RolePrivilegeRequest updateRolePrivilegeRequest)
			throws ResourceNotFoundException;

}
