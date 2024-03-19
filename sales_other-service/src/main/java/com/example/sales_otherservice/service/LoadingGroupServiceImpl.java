package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.LoadingGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.LoadingGroupMapper;
import com.example.sales_otherservice.repository.LoadingGroupRepository;
import com.example.sales_otherservice.service.interfaces.LoadingGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoadingGroupServiceImpl implements LoadingGroupService {
	private final LoadingGroupRepository loadingGroupRepository;
	private final LoadingGroupMapper loadingGroupMapper;
	private final DynamicClient dynamicClient;

	@Override
	public LoadingGroupResponse saveLg(LoadingGroupRequest loadingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(loadingGroupRequest);
		String lgCode = loadingGroupRequest.getLgCode();
		String lgName = loadingGroupRequest.getLgName();
		if (loadingGroupRepository.existsByLgCodeOrLgName(lgCode, lgName)) {
			throw new ResourceFoundException("Loading Group already Exists !!!");
		}

		LoadingGroup loadingGroup = loadingGroupMapper.mapToLoadingGroup(loadingGroupRequest);
		validateDynamicFields(loadingGroup);
		LoadingGroup savedGroup = loadingGroupRepository.save(loadingGroup);
		return loadingGroupMapper.mapToLoadingGroupResponse(savedGroup);
	}

	@Override
	public LoadingGroupResponse getLgById(@NonNull Long id) throws ResourceNotFoundException {
		LoadingGroup loadingGroup = this.findLgById(id);
		return loadingGroupMapper.mapToLoadingGroupResponse(loadingGroup);
	}

	@Override
	public List<LoadingGroupResponse> getAllLg() {
		return loadingGroupRepository.findAll().stream().sorted(Comparator.comparing(LoadingGroup::getId))
				.map(loadingGroupMapper::mapToLoadingGroupResponse).toList();
	}

	@Override
	public List<LoadingGroupResponse> findAllStatusTrue() {
		return loadingGroupRepository.findAllByLgStatusIsTrue().stream()
				.sorted(Comparator.comparing(LoadingGroup::getId)).map(loadingGroupMapper::mapToLoadingGroupResponse)
				.toList();
	}

	@Override
	public LoadingGroupResponse updateLg(@NonNull Long id, LoadingGroupRequest updateLoadingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
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
			return loadingGroupMapper.mapToLoadingGroupResponse(updatedLoadingGroup);
		}
		throw new ResourceFoundException("Loading Group already Exists !!!");
	}

	@Override
	public LoadingGroupResponse updateLgStatus(@NonNull Long id) throws ResourceNotFoundException {
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
		return loadingGroupMapper.mapToLoadingGroupResponse(savedGroup);
	}

	@Override
	public List<LoadingGroupResponse> updateBatchLgStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
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
				.map(loadingGroupMapper::mapToLoadingGroupResponse).toList();
	}

	@Override
	public void deleteLgById(@NonNull Long id) throws ResourceNotFoundException {
		LoadingGroup loadingGroup = this.findLgById(id);
		if (loadingGroup != null) {
			loadingGroupRepository.delete(loadingGroup);
		}
	}

	@Override
	public void deleteBatchLg(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<LoadingGroup> loadingGroups = this.findAllLgById(ids);
		if (!loadingGroups.isEmpty()) {
			loadingGroupRepository.deleteAll(loadingGroups);
		}
	}

	private void validateDynamicFields(LoadingGroup loadingGroup) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : loadingGroup.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = LoadingGroup.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private LoadingGroup findLgById(@NonNull Long id) throws ResourceNotFoundException {
		return loadingGroupRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Loading Group not found with this Id"));
	}

	private List<LoadingGroup> findAllLgById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<LoadingGroup> loadingGroups = loadingGroupRepository.findAllById(ids);

		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Loading Group with IDs " + missingIds + " not found.");
		}
		return loadingGroups;
	}

}
