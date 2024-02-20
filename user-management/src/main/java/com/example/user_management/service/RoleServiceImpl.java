package com.example.user_management.service;

import static com.example.user_management.utils.Constants.NO_ROLE_FOUND_WITH_ID_MESSAGE;
import static com.example.user_management.utils.Constants.ROLE_FOUND_WITH_NAME_MESSAGE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.user_management.client.PlantResponse;
import com.example.user_management.client.PlantServiceClient;
import com.example.user_management.client.RolePlantResponse;
import com.example.user_management.dto.request.RolePrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.AuditFields;
import com.example.user_management.entity.Privilege;
import com.example.user_management.entity.Role;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.repository.PrivilegeRepository;
import com.example.user_management.repository.RoleRepository;
import com.example.user_management.service.interfaces.RoleService;
import com.example.user_management.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
	private final RoleRepository roleRepository;
	private final PrivilegeRepository privilegeRepository;
	private final ModelMapper modelMapper;
	private final PlantServiceClient plantServiceClient;

	@Override
	public RoleResponse saveRole(RoleRequest roleRequest) throws ResourceFoundException {
		Helpers.inputTitleCase(roleRequest);
		boolean exists = roleRepository.existsByName(roleRequest.getName());
		if (!exists) {
			Role role = modelMapper.map(roleRequest, Role.class);
			role.setId(null);
			role.setName(roleRequest.getName());
			role.setPrivileges(setToPrivilegeId(roleRequest.getPrivileges()));
			Role savedRole = roleRepository.save(role);
			return mapToRoleResponse(savedRole);
		}
		throw new ResourceFoundException(ROLE_FOUND_WITH_NAME_MESSAGE);
	}

	@Override
	@Cacheable("roles")
	public RoleResponse getRoleById(Long id) throws ResourceNotFoundException {
		Role role = this.findRoleById(id);
		return mapToRoleResponse(role);
	}

	@Override
	public RolePlantResponse getRolePlantById(Long id) throws ResourceNotFoundException {
		Role role = this.findRoleById(id);
		return mapToRolePlantResponse(role);
	}

	@Override
	@Cacheable("roles")
	public List<RoleResponse> getAllRoles() {
		List<Role> list = roleRepository.findAll();
		return list.stream().sorted(Comparator.comparing(Role::getId)).map(this::mapToRoleResponse).toList();
	}

	@Override
	public List<RolePlantResponse> getAllRolesPlant() {
		List<Role> list = roleRepository.findAll();
		return list.stream().sorted(Comparator.comparing(Role::getId)).map(this::mapToRolePlantResponse).toList();
	}

	@Override
	@Cacheable("roles")
	public List<RoleResponse> findAllStatusTrue() {
		List<Role> roles = roleRepository.findAllByStatusIsTrue();
		return roles.stream().sorted(Comparator.comparing(Role::getId)).map(this::mapToRoleResponse).toList();
	}

	@Override
	public List<RolePlantResponse> findAllRolesPlantStatusTrue() {
		List<Role> list = roleRepository.findAllByStatusIsTrue();
		return list.stream().sorted(Comparator.comparing(Role::getId)).map(this::mapToRolePlantResponse).toList();
	}

	@Override
	public RoleResponse updateRole(Long id, RoleRequest updateRoleRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updateRoleRequest);
		Role existingRole = this.findRoleById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		boolean exists = roleRepository.existsByNameAndIdNot(updateRoleRequest.getName(), id);
		if (!exists) {
			if (!existingRole.getName().equals(updateRoleRequest.getName())) {
				auditFields.add(new AuditFields(null, "Name", existingRole.getName(), updateRoleRequest.getName()));
				existingRole.setName(updateRoleRequest.getName());
			}
			if (!existingRole.getDescription().equals(updateRoleRequest.getDescription())) {
				auditFields.add(new AuditFields(null, "Description", existingRole.getDescription(),
						updateRoleRequest.getDescription()));
				existingRole.setDescription(updateRoleRequest.getDescription());
			}
			if (!existingRole.getPlantId().equals(updateRoleRequest.getPlantId())) {
				auditFields
						.add(new AuditFields(null, "Plant", existingRole.getPlantId(), updateRoleRequest.getPlantId()));
				existingRole.setPlantId(updateRoleRequest.getPlantId());
			}
			if (!existingRole.getStatus().equals(updateRoleRequest.getStatus())) {
				auditFields
						.add(new AuditFields(null, "Status", existingRole.getStatus(), updateRoleRequest.getStatus()));
				existingRole.setStatus(updateRoleRequest.getStatus());
			}
			if (!existingRole.getPrivileges().equals(setToPrivilegeId(updateRoleRequest.getPrivileges()))) {
				auditFields.add(new AuditFields(null, "Privileges", existingRole.getPrivileges(),
						updateRoleRequest.getPrivileges()));
				existingRole.setPrivileges(setToPrivilegeId(updateRoleRequest.getPrivileges()));
			}
			existingRole.updateAuditHistory(auditFields);
			Role updatedRole = roleRepository.save(existingRole);
			return mapToRoleResponse(updatedRole);
		}
		throw new ResourceFoundException(ROLE_FOUND_WITH_NAME_MESSAGE);
	}

	@Override
	public List<RoleResponse> updateBulkStatusRoleId(List<Long> ids) throws ResourceNotFoundException {
		List<Role> existingRoles = this.findAllRolesById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingRoles.forEach(existingRole -> {
			if (existingRole.getStatus() != null) {
				auditFields.add(new AuditFields(null, "Status", existingRole.getStatus(), !existingRole.getStatus()));
				existingRole.setStatus(!existingRole.getStatus());
			}
			existingRole.updateAuditHistory(auditFields);
		});
		roleRepository.saveAll(existingRoles);
		return existingRoles.stream().map(this::mapToRoleResponse).toList();
	}

	@Override
	public RoleResponse updateStatusUsingRoleId(Long id) throws ResourceNotFoundException {
		Role existingRole = this.findRoleById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingRole.getStatus() != null) {
			auditFields.add(new AuditFields(null, "Status", existingRole.getStatus(), !existingRole.getStatus()));
			existingRole.setStatus(!existingRole.getStatus());
		}
		existingRole.updateAuditHistory(auditFields);
		Role updateRole = roleRepository.save(existingRole);
		return mapToRoleResponse(updateRole);
	}

	@Override
	public void deleteRoleId(Long id) throws ResourceNotFoundException {
		Role role = this.findRoleById(id);
		roleRepository.deleteById(role.getId());
	}

	@Override
	public void deleteBatchRole(List<Long> ids) throws ResourceNotFoundException {
		this.findAllRolesById(ids);
		roleRepository.deleteAllByIdInBatch(ids);

	}

	public Set<Privilege> setToPrivilegeId(Long[] privileges) {
		Set<Privilege> rolesPrivileges = new HashSet<>();
		for (Long privilegeId : privileges) {
			Optional<Privilege> fetchedPrivilege = privilegeRepository.findById(privilegeId);
			fetchedPrivilege.ifPresent(rolesPrivileges::add);
		}
		return rolesPrivileges;
	}

	private RoleResponse mapToRoleResponse(Role role) {
		return modelMapper.map(role, RoleResponse.class);
	}

	private RolePlantResponse mapToRolePlantResponse(Role role) {
		RolePlantResponse response = modelMapper.map(role, RolePlantResponse.class);
		if (plantServiceClient == null) {
			throw new IllegalStateException("Plant service is not initiated");
		}
		PlantResponse plant = plantServiceClient.getPlantById(role.getPlantId());
		response.setPlant(plant);
		return response;
	}

	private Role findRoleById(Long id) throws ResourceNotFoundException {
		Optional<Role> role = roleRepository.findById(id);
		if (role.isEmpty()) {
			throw new ResourceNotFoundException(NO_ROLE_FOUND_WITH_ID_MESSAGE);
		}
		return role.get();
	}

	private List<Role> findAllRolesById(List<Long> ids) throws ResourceNotFoundException {
		List<Role> roles = roleRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> roles.stream().noneMatch(entity -> entity.getId().equals(id)))
				.toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Role with IDs " + missingIds + " not found");
		}
		return roles;
	}

	@Override
	public RoleResponse removePrivilegesFromRole(Long roleId, RolePrivilegeRequest rolePrivilegeRequest)
			throws ResourceNotFoundException {
		return modifyPrivileges(roleId, rolePrivilegeRequest, "remove");
	}

	@Override

	public RoleResponse addPrivilegesToRole(Long roleId, RolePrivilegeRequest rolePrivilegeRequest)
			throws ResourceNotFoundException {
		return modifyPrivileges(roleId, rolePrivilegeRequest, "add");
	}

	private RoleResponse modifyPrivileges(Long roleId, RolePrivilegeRequest rolePrivilegeRequest, String operation)
			throws ResourceNotFoundException {
		Role role = this.findRoleById(roleId);
		Set<Privilege> existingPrivileges = role.getPrivileges();
		for (Long privilegeId : rolePrivilegeRequest.getPrivileges()) {
			Optional<Privilege> privilege = privilegeRepository.findById(privilegeId);
			privilege.ifPresent(p -> {
				if (operation.equals("remove")) {
					existingPrivileges.remove(p);
				} else {
					existingPrivileges.add(p);
				}
			});
		}
		role.setPrivileges(existingPrivileges);
		Role updatedRole = roleRepository.save(role);
		return mapToRoleResponse(updatedRole);
	}

}
