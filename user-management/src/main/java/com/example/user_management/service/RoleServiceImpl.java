package com.example.user_management.service;

import static com.example.user_management.utils.Constants.NO_ROLE_FOUND_WITH_ID_MESSAGE;
import static com.example.user_management.utils.Constants.ROLE_FOUND_WITH_NAME_MESSAGE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.user_management.client.RolePlantResponse;
import com.example.user_management.client.plant.PlantResponse;
import com.example.user_management.client.plant.PlantServiceClient;
import com.example.user_management.dto.request.RolePrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.AuditFields;
import com.example.user_management.entity.Privilege;
import com.example.user_management.entity.Role;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.mapping.RoleMapper;
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
	private final RoleMapper roleMapper;
	private final PlantServiceClient plantServiceClient;

	@Override
	public RoleResponse saveRole(RoleRequest roleRequest) throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(roleRequest);
		String roleName = roleRequest.getName();
		if (roleRepository.existsByName(roleName)) {
			throw new ResourceFoundException(ROLE_FOUND_WITH_NAME_MESSAGE);
		}
		Role role = roleMapper.mapToRole(roleRequest);
		role.setId(null);
		role.setName(roleName);
		role.setPrivileges(setToPrivilegeId(roleRequest.getPrivileges()));
		Role savedRole = roleRepository.save(role);
		return roleMapper.mapToRoleResponse(savedRole);
	}

	@Override
	public RoleResponse getRoleById(@NonNull Long id) throws ResourceNotFoundException {
		Role role = this.findRoleById(id);
		return roleMapper.mapToRoleResponse(role);
	}

	@Override
	public RolePlantResponse getRolePlantById(@NonNull Long id) throws ResourceNotFoundException {
		Role role = this.findRoleById(id);
		return mapToRolePlantResponse(role);
	}

	@Override
	public List<RoleResponse> getAllRoles() {
		return roleRepository.findAll().stream().sorted(Comparator.comparing(Role::getId))
				.map(roleMapper::mapToRoleResponse).toList();
	}

	@Override
	public List<RolePlantResponse> getAllRolesPlant() {
		return roleRepository.findAll().stream().sorted(Comparator.comparing(Role::getId))
				.map(this::mapToRolePlantResponse).toList();
	}

	@Override
	public List<RoleResponse> findAllStatusTrue() {
		return roleRepository.findAllByStatusIsTrue().stream().sorted(Comparator.comparing(Role::getId))
				.map(roleMapper::mapToRoleResponse).toList();
	}

	@Override
	public List<RolePlantResponse> findAllRolesPlantStatusTrue() {
		return roleRepository.findAllByStatusIsTrue().stream().sorted(Comparator.comparing(Role::getId))
				.map(this::mapToRolePlantResponse).toList();
	}

	public List<RoleResponse> findAllByPlantId(Long plantId) {
		return roleRepository.findByPlantId(plantId).stream().sorted(Comparator.comparing(Role::getId))
				.map(roleMapper::mapToRoleResponse).toList();
	}

	public List<RolePlantResponse> findAllRolesPlantByPlantId(Long plantId) {
		return roleRepository.findByPlantId(plantId).stream().sorted(Comparator.comparing(Role::getId))
				.map(this::mapToRolePlantResponse).toList();
	}

	@Override
	public RoleResponse updateRole(@NonNull Long id, RoleRequest updateRoleRequest)
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
			return roleMapper.mapToRoleResponse(updatedRole);
		}
		throw new ResourceFoundException(ROLE_FOUND_WITH_NAME_MESSAGE);
	}

	@Override
	public List<RoleResponse> updateBulkStatusRoleId(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<Role> existingRoles = this.findAllRolesById(ids);
		List<AuditFields> auditFields = new ArrayList<>();
		existingRoles.forEach(existingRole -> {
			if (existingRole.getStatus() != null) {
				auditFields.add(new AuditFields(null, "Status", existingRole.getStatus(), !existingRole.getStatus()));
				existingRole.setStatus(!existingRole.getStatus());
			}
			existingRole.updateAuditHistory(auditFields);
		});
		roleRepository.saveAll(existingRoles);
		return existingRoles.stream().map(roleMapper::mapToRoleResponse).toList();
	}

	@Override
	public RoleResponse updateStatusUsingRoleId(@NonNull Long id) throws ResourceNotFoundException {
		Role existingRole = this.findRoleById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingRole.getStatus() != null) {
			auditFields.add(new AuditFields(null, "Status", existingRole.getStatus(), !existingRole.getStatus()));
			existingRole.setStatus(!existingRole.getStatus());
		}
		existingRole.updateAuditHistory(auditFields);
		Role updateRole = roleRepository.save(existingRole);
		return roleMapper.mapToRoleResponse(updateRole);
	}

	@Override
	public void deleteRoleId(@NonNull Long id) throws ResourceNotFoundException {
		Role role = this.findRoleById(id);
		if (role != null) {
			roleRepository.delete(role);
		}
	}

	@Override
	public void deleteBatchRole(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<Role> privileges = this.findAllRolesById(ids);
		if (privileges != null) {
			roleRepository.deleteAll(privileges);
		}

	}

	public Set<Privilege> setToPrivilegeId(Long[] privileges) {
		Set<Long> privilegeIds = new HashSet<>(Arrays.asList(privileges));
		List<Privilege> fetchedPrivileges = privilegeRepository.findAllById(privilegeIds);
		return new HashSet<>(fetchedPrivileges);
	}

	private RolePlantResponse mapToRolePlantResponse(Role role) {
		RolePlantResponse response = roleMapper.mapToRolePlantResponse(role);
		if (plantServiceClient == null) {
			throw new IllegalStateException("Plant service is not initiated");
		}
		PlantResponse plant = plantServiceClient.getPlantById(role.getPlantId());
		response.setPlant(plant);
		return response;
	}

	private Role findRoleById(@NonNull Long id) throws ResourceNotFoundException {
		Optional<Role> role = roleRepository.findById(id);
		if (role.isEmpty()) {
			throw new ResourceNotFoundException(NO_ROLE_FOUND_WITH_ID_MESSAGE);
		}
		return role.get();
	}

	private List<Role> findAllRolesById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<Role> roles = roleRepository.findAllById(ids);

		Map<Long, Role> roleMap = roles.stream().collect(Collectors.toMap(Role::getId, Function.identity()));

		List<Long> missingIds = ids.stream().filter(id -> !roleMap.containsKey(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Role with IDs " + missingIds + " not found");
		}

		return ids.stream().map(roleMap::get).toList();
	}

	@Override
	public RoleResponse removePrivilegesFromRole(@NonNull Long roleId, RolePrivilegeRequest rolePrivilegeRequest)
			throws ResourceNotFoundException {
		return modifyPrivileges(roleId, rolePrivilegeRequest, "remove");
	}

	@Override

	public RoleResponse addPrivilegesToRole(@NonNull Long roleId, RolePrivilegeRequest rolePrivilegeRequest)
			throws ResourceNotFoundException {
		return modifyPrivileges(roleId, rolePrivilegeRequest, "add");
	}

	private RoleResponse modifyPrivileges(@NonNull Long roleId, RolePrivilegeRequest rolePrivilegeRequest,
			String operation) throws ResourceNotFoundException {
		Role role = this.findRoleById(roleId);
		Set<Privilege> existingPrivileges = role.getPrivileges();
		for (Long privilegeId : rolePrivilegeRequest.getPrivileges()) {
			if (privilegeId != null) {
				Optional<Privilege> privilege = privilegeRepository.findById(privilegeId);
				privilege.ifPresent(p -> {
					if (operation.equals("remove")) {
						existingPrivileges.remove(p);
					} else {
						existingPrivileges.add(p);
					}
				});
			}
		}
		role.setPrivileges(existingPrivileges);
		Role updatedRole = roleRepository.save(role);
		return roleMapper.mapToRoleResponse(updatedRole);
	}

}
