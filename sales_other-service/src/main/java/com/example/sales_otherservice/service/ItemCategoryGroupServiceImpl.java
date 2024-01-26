package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.ItemCategoryGroupRequest;
import com.example.sales_otherservice.dto.response.ItemCategoryGroupResponse;
import com.example.sales_otherservice.entity.ItemCategoryGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.ItemCategoryGroupRepository;
import com.example.sales_otherservice.service.interfaces.ItemCategoryGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemCategoryGroupServiceImpl implements ItemCategoryGroupService {
	private final ItemCategoryGroupRepository itemCategoryGroupRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public ItemCategoryGroupResponse saveIcg(ItemCategoryGroupRequest itemCategoryGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String icgCode = itemCategoryGroupRequest.getIcgCode();
		String icgName = itemCategoryGroupRequest.getIcgName();
		boolean exists = itemCategoryGroupRepository.existsByIcgCodeOrIcgName(icgCode, icgName);
		if (!exists) {

			ItemCategoryGroup categoryGroup = modelMapper.map(itemCategoryGroupRequest, ItemCategoryGroup.class);
			for (Map.Entry<String, Object> entryField : categoryGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ItemCategoryGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			ItemCategoryGroup savedGroup = itemCategoryGroupRepository.save(categoryGroup);
			return mapToItemCategoryGroupResponse(savedGroup);
		}
		throw new ResourceFoundException("Item Category Group Already Exist");
	}

	@Override
	public ItemCategoryGroupResponse getIcgById(Long id) throws ResourceNotFoundException {
		ItemCategoryGroup itemCategoryGroup = this.findIcgById(id);
		return mapToItemCategoryGroupResponse(itemCategoryGroup);
	}

	@Override
	public List<ItemCategoryGroupResponse> getAllIcg() {
		List<ItemCategoryGroup> categoryGroups = itemCategoryGroupRepository.findAll();
		return categoryGroups.stream().sorted(Comparator.comparing(ItemCategoryGroup::getId))
				.map(this::mapToItemCategoryGroupResponse).toList();
	}

	@Override
	public List<ItemCategoryGroupResponse> findAllStatusTrue() {
		List<ItemCategoryGroup> categoryGroups = itemCategoryGroupRepository.findAllByIcgStatusIsTrue();
		return categoryGroups.stream().sorted(Comparator.comparing(ItemCategoryGroup::getId))
				.map(this::mapToItemCategoryGroupResponse).toList();
	}

	@Override
	public ItemCategoryGroupResponse updateIcg(Long id, ItemCategoryGroupRequest updateItemCategoryGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		String icgCode = updateItemCategoryGroupRequest.getIcgCode();
		String icgName = updateItemCategoryGroupRequest.getIcgName();
		ItemCategoryGroup existingCategoryGroup = this.findIcgById(id);
		boolean exists = itemCategoryGroupRepository.existsByIcgCodeAndIdNotOrIcgNameAndIdNot(icgCode, id, icgName, id);
		if (!exists) {
			modelMapper.map(updateItemCategoryGroupRequest, existingCategoryGroup);
			for (Map.Entry<String, Object> entryField : existingCategoryGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ItemCategoryGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			ItemCategoryGroup updatedGroup = itemCategoryGroupRepository.save(existingCategoryGroup);
			return mapToItemCategoryGroupResponse(updatedGroup);
		}
		throw new ResourceFoundException("Item Category Group Already Exist");
	}

	@Override
	public ItemCategoryGroupResponse updateIcgStatus(Long id) throws ResourceNotFoundException {
		ItemCategoryGroup categoryGroup = this.findIcgById(id);
		categoryGroup.setIcgStatus(!categoryGroup.getIcgStatus());
		itemCategoryGroupRepository.save(categoryGroup);
		return mapToItemCategoryGroupResponse(categoryGroup);
	}

	@Override
	public List<ItemCategoryGroupResponse> updateBatchIcgStatus(List<Long> ids) throws ResourceNotFoundException {
		List<ItemCategoryGroup> groups = this.findAllIcgById(ids);
		groups.forEach(group -> group.setIcgStatus(!group.getIcgStatus()));
		itemCategoryGroupRepository.saveAll(groups);
		return groups.stream().map(this::mapToItemCategoryGroupResponse).toList();
	}

	@Override
	public void deleteIcgById(Long id) throws ResourceNotFoundException {
		ItemCategoryGroup categoryGroup = this.findIcgById(id);
		itemCategoryGroupRepository.deleteById(categoryGroup.getId());
	}

	@Override
	public void deleteBatchIcg(List<Long> ids) throws ResourceNotFoundException {
		this.findAllIcgById(ids);
		itemCategoryGroupRepository.deleteAllByIdInBatch(ids);
	}

	private ItemCategoryGroupResponse mapToItemCategoryGroupResponse(ItemCategoryGroup itemCategoryGroup) {
		return modelMapper.map(itemCategoryGroup, ItemCategoryGroupResponse.class);
	}

	private ItemCategoryGroup findIcgById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<ItemCategoryGroup> categoryGroup = itemCategoryGroupRepository.findById(id);
		if (categoryGroup.isEmpty()) {
			throw new ResourceNotFoundException("Item Category Group not found with this Id");
		}
		return categoryGroup.get();
	}

	private List<ItemCategoryGroup> findAllIcgById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ItemCategoryGroup> groups = itemCategoryGroupRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> groups.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Item Category Group with IDs " + missingIds + " not found.");
		}
		return groups;
	}

}
