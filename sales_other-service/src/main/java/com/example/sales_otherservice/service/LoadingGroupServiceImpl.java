package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.LoadingGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.LoadingGroupRepository;
import com.example.sales_otherservice.service.interfaces.LoadingGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoadingGroupServiceImpl implements LoadingGroupService {
	private final LoadingGroupRepository loadingGroupRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public LoadingGroupResponse saveLg(LoadingGroupRequest loadingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(loadingGroupRequest);
		String lgCode = loadingGroupRequest.getLgCode();
		String lgName = loadingGroupRequest.getLgName();
		boolean exists = loadingGroupRepository.existsByLgCodeOrLgName(lgCode, lgName);
		if (!exists) {

			LoadingGroup loadingGroup = modelMapper.map(loadingGroupRequest, LoadingGroup.class);
			for (Map.Entry<String, Object> entryField : loadingGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = LoadingGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			LoadingGroup savedGroup = loadingGroupRepository.save(loadingGroup);
			return mapToLoadingGroupResponse(savedGroup);
		}
		throw new ResourceFoundException("Loading Group already Exists !!!");
	}

	@Override
	public LoadingGroupResponse getLgById(Long id) throws ResourceNotFoundException {
		LoadingGroup loadingGroup = this.findLgById(id);
		return mapToLoadingGroupResponse(loadingGroup);
	}

	@Override
	public List<LoadingGroupResponse> getAllLg() {
		List<LoadingGroup> loadingGroups = loadingGroupRepository.findAll();
		return loadingGroups.stream().sorted(Comparator.comparing(LoadingGroup::getId))
				.map(this::mapToLoadingGroupResponse).toList();
	}

	@Override
	public List<LoadingGroupResponse> findAllStatusTrue() {
		List<LoadingGroup> loadingGroups = loadingGroupRepository.findAllByLgStatusIsTrue();
		return loadingGroups.stream().sorted(Comparator.comparing(LoadingGroup::getId))
				.map(this::mapToLoadingGroupResponse).toList();
	}

	@Override
	public LoadingGroupResponse updateLg(Long id, LoadingGroupRequest updateLoadingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateLoadingGroupRequest);
		String lgCode = updateLoadingGroupRequest.getLgCode();
		String lgName = updateLoadingGroupRequest.getLgName();
		LoadingGroup existingLoadingGroup = this.findLgById(id);
		boolean exists = loadingGroupRepository.existsByLgCodeAndIdNotOrLgNameAndIdNot(lgCode, id, lgName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingLoadingGroup.getLgCode().equals(lgCode)) {
				auditFields.add(new AuditFields(null, "Lg Code", existingLoadingGroup.getLgCode(), lgCode));
				existingLoadingGroup.setLgCode(lgCode);
			}
			if (!existingLoadingGroup.getLgName().equals(lgName)) {
				auditFields.add(new AuditFields(null, "Lg Name", existingLoadingGroup.getLgName(), lgName));
				existingLoadingGroup.setLgName(lgName);
			}
			if (!existingLoadingGroup.getLgStatus().equals(updateLoadingGroupRequest.getLgStatus())) {
				auditFields.add(new AuditFields(null, "Lg Status", existingLoadingGroup.getLgStatus(),
						updateLoadingGroupRequest.getLgStatus()));
				existingLoadingGroup.setLgStatus(updateLoadingGroupRequest.getLgStatus());
			}
			if (!existingLoadingGroup.getDynamicFields().equals(updateLoadingGroupRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateLoadingGroupRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingLoadingGroup.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingLoadingGroup.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingLoadingGroup.updateAuditHistory(auditFields);
			LoadingGroup updatedLoadingGroup = loadingGroupRepository.save(existingLoadingGroup);
			return mapToLoadingGroupResponse(updatedLoadingGroup);
		}
		throw new ResourceFoundException("Loading Group already Exists !!!");
	}

	@Override
	public LoadingGroupResponse updateLgStatus(Long id) throws ResourceNotFoundException {
		LoadingGroup existingLoadingGroup = this.findLgById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingLoadingGroup.getLgStatus() != null) {
			auditFields.add(new AuditFields(null, "Lg Status", existingLoadingGroup.getLgStatus(),
					!existingLoadingGroup.getLgStatus()));
			existingLoadingGroup.setLgStatus(!existingLoadingGroup.getLgStatus());
		}
		existingLoadingGroup.updateAuditHistory(auditFields);
		LoadingGroup savedGroup = loadingGroupRepository.save(existingLoadingGroup);
		return mapToLoadingGroupResponse(savedGroup);
	}

	@Override
	public List<LoadingGroupResponse> updateBatchLgStatus(List<Long> ids) throws ResourceNotFoundException {
		List<LoadingGroup> loadingGroups = this.findAllLgById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		loadingGroups.forEach(existingLoadingGroup -> {
			if (existingLoadingGroup.getLgStatus() != null) {
				auditFields.add(new AuditFields(null, "Lg Status", existingLoadingGroup.getLgStatus(),
						!existingLoadingGroup.getLgStatus()));
				existingLoadingGroup.setLgStatus(!existingLoadingGroup.getLgStatus());
			}
			existingLoadingGroup.updateAuditHistory(auditFields);

		});
		loadingGroupRepository.saveAll(loadingGroups);
		return loadingGroups.stream().sorted(Comparator.comparing(LoadingGroup::getId))
				.map(this::mapToLoadingGroupResponse).toList();
	}

	@Override
	public void deleteLgById(Long id) throws ResourceNotFoundException {
		LoadingGroup loadingGroup = this.findLgById(id);
		loadingGroupRepository.deleteById(loadingGroup.getId());
	}

	@Override
	public void deleteBatchLg(List<Long> ids) throws ResourceNotFoundException {
		this.findAllLgById(ids);
		loadingGroupRepository.deleteAllByIdInBatch(ids);
	}

	private LoadingGroupResponse mapToLoadingGroupResponse(LoadingGroup loadingGroup) {
		return modelMapper.map(loadingGroup, LoadingGroupResponse.class);
	}

	private LoadingGroup findLgById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<LoadingGroup> loadingGroup = loadingGroupRepository.findById(id);
		if (loadingGroup.isEmpty()) {
			throw new ResourceNotFoundException("Loading Group not found with this Id");
		}
		return loadingGroup.get();
	}

	private List<LoadingGroup> findAllLgById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<LoadingGroup> loadingGroups = loadingGroupRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> loadingGroups.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Loading Group with IDs " + missingIds + " not found.");
		}
		return loadingGroups;
	}

}
