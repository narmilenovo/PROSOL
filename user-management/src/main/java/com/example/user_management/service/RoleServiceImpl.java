package com.example.user_management.service;

import com.example.user_management.dto.request.RolePrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.entity.Privilege;
import com.example.user_management.entity.Role;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.repository.PrivilegeRepository;
import com.example.user_management.repository.RoleRepository;
import com.example.user_management.service.interfaces.RoleService;
import com.example.user_management.utils.Helpers;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.example.user_management.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PrivilegeRepository privilegeRepository;
    private final ModelMapper modelMapper;

    @Override
    public RoleResponse saveRole(RoleRequest roleRequest) throws ResourceFoundException {
        String roleName = Helpers.capitalize(roleRequest.getName());
        boolean exists = roleRepository.existsByName(roleName);
        if (!exists) {
            Role role = modelMapper.map(roleRequest, Role.class);
            role.setName(roleName);
            role.setPrivileges(setToString(roleRequest.getPrivileges()));
            Role savedRole = roleRepository.save(role);
            return mapToRoleResponse(savedRole);
        }
        throw new ResourceFoundException(ROLE_FOUND_WITH_NAME_MESSAGE);
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        List<Role> list = roleRepository.findAll();
        return list.stream().map(this::mapToRoleResponse).toList();
    }

    @Override
    public RoleResponse getRoleById(Long id) throws ResourceNotFoundException {
        Role role = this.findRoleById(id);
        return mapToRoleResponse(role);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest updateRoleRequest) throws ResourceNotFoundException, ResourceFoundException {
        String roleName = Helpers.capitalize(updateRoleRequest.getName());
        Role existingRole = this.findRoleById(id);
        boolean exists = roleRepository.existsByName(roleName);
        if (!exists) {
            modelMapper.map(updateRoleRequest, existingRole);
            existingRole.setName(roleName);
            existingRole.setPrivileges(setToString(updateRoleRequest.getPrivileges()));
            Role updatedRole = roleRepository.save(existingRole);
            return mapToRoleResponse(updatedRole);
        }
        throw new ResourceFoundException(ROLE_FOUND_WITH_NAME_MESSAGE);
    }

    @Override
    public List<RoleResponse> updateBulkStatusRoleId(List<Long> id) {
        List<Role> existingRoles = roleRepository.findAllById(id);
        for (Role role : existingRoles) {
            role.setStatus(!role.getStatus());
        }
        roleRepository.saveAll(existingRoles);
        return existingRoles.stream().map(this::mapToRoleResponse).toList();
    }

    @Override
    public RoleResponse updateStatusUsingRoleId(Long id) throws ResourceNotFoundException {
        Role existingRole = this.findRoleById(id);
        existingRole.setStatus(!existingRole.getStatus());
        Role updateRole = roleRepository.save(existingRole);
        return mapToRoleResponse(updateRole);
    }

    @Override
    public void deleteRoleId(Long id) throws ResourceNotFoundException {
        Role role = this.findRoleById(id);
        roleRepository.deleteById(role.getId());
    }

    @Override
    public void deleteBatchRole(List<Long> id) {
        List<Role> roles = roleRepository.findAllById(id);
        roleRepository.deleteAll(roles);

    }

    @Override
    public List<RoleResponse> findAllStatusTrue() {
        List<Role> roles = roleRepository.findAllByStatusIsTrue();
        return roles.stream().map(this::mapToRoleResponse).toList();
    }


    public Set<Privilege> setToString(String[] privileges) {
        Set<Privilege> rolesPrivileges = new HashSet<>();
        for (String privilegeName : privileges) {
            Optional<Privilege> fetchedPrivilege = privilegeRepository.findByName(Helpers.capitalize(privilegeName));
            fetchedPrivilege.ifPresent(rolesPrivileges::add);
        }
        return rolesPrivileges;
    }


    private RoleResponse mapToRoleResponse(Role role) {
        return modelMapper.map(role, RoleResponse.class);
    }


    private Role findRoleById(Long id) throws ResourceNotFoundException {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isEmpty()) {
            throw new ResourceNotFoundException(NO_ROLE_FOUND_WITH_ID_MESSAGE);
        }
        return role.get();
    }

    private Role findRoleByName(String name) throws ResourceNotFoundException {
        Optional<Role> role = roleRepository.findByName(name);

        if (role.isEmpty()) {
            throw new ResourceNotFoundException(NO_ROLE_FOUND_WITH_NAME_MESSAGE);
        }

        return role.get();
    }


    @Override
    public RoleResponse removePrivilegesFromRole(Long roleId, RolePrivilegeRequest rolePrivilegeRequest) throws ResourceNotFoundException {
        return modifyPrivileges(roleId, rolePrivilegeRequest, "remove");
    }

    @Override

    public RoleResponse addPrivilegesToRole(Long roleId, RolePrivilegeRequest rolePrivilegeRequest) throws ResourceNotFoundException {
        return modifyPrivileges(roleId, rolePrivilegeRequest, "add");
    }

    private RoleResponse modifyPrivileges(Long roleId, RolePrivilegeRequest rolePrivilegeRequest, String operation) throws ResourceNotFoundException {
        Role role = this.findRoleById(roleId);
        Set<Privilege> existingPrivileges = role.getPrivileges();
        for (String privilegeName : rolePrivilegeRequest.getPrivileges()) {
            Optional<Privilege> privilege = privilegeRepository.findByName(Helpers.capitalize(privilegeName));
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
