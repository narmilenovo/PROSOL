package com.example.user_management.service;

import static com.example.user_management.utils.Constants.NO_PRIVILEGE_FOUND_WITH_ID_MESSAGE;
import static com.example.user_management.utils.Constants.PRIVILEGE_FOUND_WITH_NAME_MESSAGE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.response.PrivilegeResponse;
import com.example.user_management.entity.AuditFields;
import com.example.user_management.entity.Privilege;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.mapping.PrivilegeMapper;
import com.example.user_management.repository.PrivilegeRepository;
import com.example.user_management.service.interfaces.PrivilegeService;
import com.example.user_management.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrivilegeServiceImpl implements PrivilegeService {
	private final PrivilegeRepository privilegeRepository;
	private final PrivilegeMapper privilegeMapper;

	@Override
	public PrivilegeResponse savePrivilege(PrivilegeRequest privilegeRequest) throws ResourceFoundException {
		Helpers.inputTitleCase(privilegeRequest);
		String privilegeName = privilegeRequest.getName();
		if (privilegeRepository.existsByName(privilegeName)) {
			throw new ResourceFoundException(PRIVILEGE_FOUND_WITH_NAME_MESSAGE);
		}
		Privilege privilege = privilegeMapper.mapToPrivilege(privilegeRequest);
		privilege.setName(privilegeName);
		Privilege savedPrivilege = privilegeRepository.save(privilege);
		return privilegeMapper.mapToPrivilegeResponse(savedPrivilege);
	}

	@Override
	public List<PrivilegeResponse> saveAllPrivileges(List<PrivilegeRequest> privilegeRequests)
			throws ResourceFoundException {
		List<PrivilegeResponse> privilegeResponses = new ArrayList<>();
		for (PrivilegeRequest request : privilegeRequests) {
			PrivilegeResponse response = this.savePrivilege(request);
			privilegeResponses.add(response);
		}
		return privilegeResponses;
	}

	@Override
	public PrivilegeResponse getPrivilegeById(@NonNull Long id) throws ResourceNotFoundException {
		Privilege privilege = this.findPrivilegeById(id);
		return privilegeMapper.mapToPrivilegeResponse(privilege);
	}

	@Override
	public List<PrivilegeResponse> getAllPrivileges() {
		return privilegeRepository.findAll().stream().sorted(Comparator.comparing(Privilege::getId))
				.map(privilegeMapper::mapToPrivilegeResponse).toList();
	}

	@Override
	@Transactional
	public PrivilegeResponse updatePrivilege(@NonNull Long id, PrivilegeRequest updatePrivilegeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updatePrivilegeRequest);
		String privilegeName = updatePrivilegeRequest.getName();

		Privilege existingPrivilege = this.findPrivilegeById(id);
		List<AuditFields> auditFields = new ArrayList<>();
		boolean exists = privilegeRepository.existsByNameAndIdNot(privilegeName, id);
		if (!exists) {
			if (!existingPrivilege.getName().equals(privilegeName)) {
				auditFields.add(new AuditFields(null, "Name", existingPrivilege.getName(), privilegeName));
				existingPrivilege.setName(privilegeName);
			}
			if (!existingPrivilege.getStatus().equals(updatePrivilegeRequest.getStatus())) {
				auditFields.add(new AuditFields(null, "Status", existingPrivilege.getStatus(),
						updatePrivilegeRequest.getStatus()));
				existingPrivilege.setStatus(updatePrivilegeRequest.getStatus());
			}
			existingPrivilege.updateAuditHistory(auditFields);
			Privilege updatedPrivilege = privilegeRepository.save(existingPrivilege);
			return privilegeMapper.mapToPrivilegeResponse(updatedPrivilege);
		}
		throw new ResourceFoundException(PRIVILEGE_FOUND_WITH_NAME_MESSAGE);
	}

	@Override
	public PrivilegeResponse updateStatusUsingPrivilegeById(@NonNull Long id) throws ResourceNotFoundException {
		Privilege existingPrivilege = this.findPrivilegeById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingPrivilege.getStatus() != null) {
			auditFields.add(
					new AuditFields(null, "Status", existingPrivilege.getStatus(), !existingPrivilege.getStatus()));
			existingPrivilege.setStatus(!existingPrivilege.getStatus());
		}
		existingPrivilege.updateAuditHistory(auditFields);
		Privilege updatedPrivilege = privilegeRepository.save(existingPrivilege);
		return privilegeMapper.mapToPrivilegeResponse(updatedPrivilege);
	}

	@Override
	public List<PrivilegeResponse> updateBulkStatusPrivilegeById(@NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<Privilege> existingPrivileges = this.findAllPrivilegeById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingPrivileges.forEach(existingPrivilege -> {
			if (existingPrivilege.getStatus() != null) {
				auditFields.add(
						new AuditFields(null, "Status", existingPrivilege.getStatus(), !existingPrivilege.getStatus()));
				existingPrivilege.setStatus(!existingPrivilege.getStatus());
			}
			existingPrivilege.updateAuditHistory(auditFields);
		});
		privilegeRepository.saveAll(existingPrivileges);
		return existingPrivileges.stream().map(privilegeMapper::mapToPrivilegeResponse).toList();
	}

	@Override
	public void deletePrivilege(@NonNull Long id) throws ResourceNotFoundException {
		Privilege privilege = this.findPrivilegeById(id);
		if (privilege != null) {
			privilegeRepository.delete(privilege);
		}
	}

	@Override
	public void deleteBatchPrivilege(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<Privilege> privileges = this.findAllPrivilegeById(ids);
		privilegeRepository.deleteAll(privileges);
	}

	private Privilege findPrivilegeById(@NonNull Long id) throws ResourceNotFoundException {
		return privilegeRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(NO_PRIVILEGE_FOUND_WITH_ID_MESSAGE));

	}

	@NonNull
	private List<Privilege> findAllPrivilegeById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<Privilege> privileges = privilegeRepository.findAllById(ids);

		idSet.removeAll(privileges.stream().map(Privilege::getId).collect(Collectors.toSet()));
		if (!idSet.isEmpty()) {
			throw new ResourceNotFoundException("Privilege with IDs " + idSet + " not found.");
		}
		return privileges;
	}

}
