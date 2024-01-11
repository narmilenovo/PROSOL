package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.DynamicClient;
import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.entity.LoadingGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.LoadingGroupRepository;
import com.example.sales_otherservice.service.interfaces.LoadingGroupService;

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
	public List<LoadingGroupResponse> getAllLg() {
		List<LoadingGroup> loadingGroups = loadingGroupRepository.findAll();
		return loadingGroups.stream().sorted(Comparator.comparing(LoadingGroup::getId))
				.map(this::mapToLoadingGroupResponse).toList();
	}

	@Override
	public LoadingGroupResponse getLgById(Long id) throws ResourceNotFoundException {
		LoadingGroup loadingGroup = this.findLgById(id);
		return mapToLoadingGroupResponse(loadingGroup);
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
		String lgCode = updateLoadingGroupRequest.getLgCode();
		String lgName = updateLoadingGroupRequest.getLgName();
		LoadingGroup existingLoadingGroup = this.findLgById(id);
		boolean exists = loadingGroupRepository.existsByLgCodeAndIdNotOrLgNameAndIdNot(lgCode, id, lgName, id);
		if (!exists) {
			modelMapper.map(updateLoadingGroupRequest, existingLoadingGroup);
			for (Map.Entry<String, Object> entryField : existingLoadingGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = LoadingGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			LoadingGroup updatedLoadingGroup = loadingGroupRepository.save(existingLoadingGroup);
			return mapToLoadingGroupResponse(updatedLoadingGroup);
		}
		throw new ResourceFoundException("Loading Group already Exists !!!");
	}

	@Override
	public void deleteLgById(Long id) throws ResourceNotFoundException {
		LoadingGroup loadingGroup = this.findLgById(id);
		loadingGroupRepository.deleteById(loadingGroup.getId());
	}

	@Override
	public void deleteBatchLg(List<Long> ids) {
		loadingGroupRepository.deleteAllByIdInBatch(ids);
	}

	private LoadingGroupResponse mapToLoadingGroupResponse(LoadingGroup loadingGroup) {
		return modelMapper.map(loadingGroup, LoadingGroupResponse.class);
	}

	private LoadingGroup findLgById(Long id) throws ResourceNotFoundException {
		Optional<LoadingGroup> loadingGroup = loadingGroupRepository.findById(id);
		if (loadingGroup.isEmpty()) {
			throw new ResourceNotFoundException("Loading Group not found with this Id");
		}
		return loadingGroup.get();
	}

}
