package com.example.user_management.service;

import static com.example.user_management.utils.Constants.NO_PRIVILEGE_FOUND_WITH_ID_MESSAGE;
import static com.example.user_management.utils.Constants.PRIVILEGE_FOUND_WITH_NAME_MESSAGE;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.response.PrivilegeResponse;
import com.example.user_management.entity.Privilege;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.repository.PrivilegeRepository;
import com.example.user_management.service.interfaces.PrivilegeService;
import com.example.user_management.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PrivilegeServiceImpl implements PrivilegeService {
	private final PrivilegeRepository privilegeRepository;
	private final ModelMapper modelMapper;

	@Override
	public PrivilegeResponse savePrivilege(PrivilegeRequest privilegeRequest) throws ResourceFoundException {
		String privilegeName = Helpers.capitalize(privilegeRequest.getName());
		boolean exists = privilegeRepository.existsByName(privilegeName);
		if (!exists) {
			Privilege privilege = modelMapper.map(privilegeRequest, Privilege.class);
			privilege.setName(privilegeName);
			Privilege savedPrivilege = privilegeRepository.save(privilege);
			return mapToPrivilegeResponse(savedPrivilege);
		}
		throw new ResourceFoundException(PRIVILEGE_FOUND_WITH_NAME_MESSAGE);
	}

	@Override
	@Cacheable("privileges")
	public PrivilegeResponse getPrivilegeById(Long id) throws ResourceNotFoundException {
		Privilege privilege = this.findPrivilegeById(id);
		return mapToPrivilegeResponse(privilege);
	}

	@Override
	@Cacheable("privileges")
	public List<PrivilegeResponse> getAllPrivileges() {
		List<Privilege> privileges = privilegeRepository.findAll();
		return privileges.stream().sorted(Comparator.comparing(Privilege::getId)).map(this::mapToPrivilegeResponse)
				.toList();
	}

	@Override
	@Transactional
	public PrivilegeResponse updatePrivilege(Long id, PrivilegeRequest updatePrivilegeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		String privilegeName = Helpers.capitalize(updatePrivilegeRequest.getName());
		Privilege existingPrivilege = this.findPrivilegeById(id);
		boolean exists = privilegeRepository.existsByNameAndIdNot(privilegeName, id);
		if (!exists) {
			modelMapper.map(updatePrivilegeRequest, existingPrivilege);
			existingPrivilege.setName(privilegeName);
			Privilege updatedPrivilege = privilegeRepository.save(existingPrivilege);
			return mapToPrivilegeResponse(updatedPrivilege);
		}
		throw new ResourceFoundException(PRIVILEGE_FOUND_WITH_NAME_MESSAGE);
	}

	@Override
	public PrivilegeResponse updateStatusUsingPrivilegeById(Long id) throws ResourceNotFoundException {
		Privilege privilege = this.findPrivilegeById(id);
		privilege.setStatus(!privilege.getStatus());
		Privilege updatedPrivilege = privilegeRepository.save(privilege);
		return mapToPrivilegeResponse(updatedPrivilege);
	}

	@Override
	public List<PrivilegeResponse> updateBulkStatusPrivilegeById(List<Long> ids) throws ResourceNotFoundException {
		List<Privilege> privileges = this.findAllPrivilegeById(ids);
		privileges.forEach(privilege -> privilege.setStatus(!privilege.getStatus()));
		privilegeRepository.saveAll(privileges);
		return privileges.stream().map(this::mapToPrivilegeResponse).toList();
	}

	@Override
	public void deletePrivilege(Long id) throws ResourceNotFoundException {
		Privilege privilege = this.findPrivilegeById(id);
		privilegeRepository.deleteById(privilege.getId());
	}

	@Override
	public void deleteBatchPrivilege(List<Long> ids) throws ResourceNotFoundException {
		this.findAllPrivilegeById(ids);
		privilegeRepository.deleteAllByIdInBatch(ids);
	}

	private PrivilegeResponse mapToPrivilegeResponse(Privilege privilege) {
		return modelMapper.map(privilege, PrivilegeResponse.class);
	}

	private Privilege findPrivilegeById(Long id) throws ResourceNotFoundException {
		Optional<Privilege> privilege = privilegeRepository.findById(id);
		if (privilege.isEmpty()) {
			throw new ResourceNotFoundException(NO_PRIVILEGE_FOUND_WITH_ID_MESSAGE);
		}
		return privilege.get();
	}

	private List<Privilege> findAllPrivilegeById(List<Long> ids) throws ResourceNotFoundException {
		List<Privilege> privileges = privilegeRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> privileges.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Privilege with IDs " + missingIds + " not found.");
		}
		return privileges;
	}

}
