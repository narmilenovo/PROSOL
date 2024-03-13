package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.TransportationGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.TransportationGroupMapper;
import com.example.sales_otherservice.repository.TransportationGroupRepository;
import com.example.sales_otherservice.service.interfaces.TransportationGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransportationGroupServiceImpl implements TransportationGroupService {
	private final TransportationGroupRepository transportationGroupRepository;
	private final TransportationGroupMapper transportationGroupMapper;
	private final DynamicClient dynamicClient;

	@Override
	public TransportationGroupResponse saveTg(TransportationGroupRequest transportationGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(transportationGroupRequest);
		String tgCode = transportationGroupRequest.getTgCode();
		String tgName = transportationGroupRequest.getTgName();
		boolean exists = transportationGroupRepository.existsByTgCodeOrTgName(tgCode, tgName);
		if (!exists) {
			TransportationGroup group = transportationGroupMapper.mapToTransportationGroup(transportationGroupRequest);

			validateDynamicFields(group);

			TransportationGroup savedGroup = transportationGroupRepository.save(group);
			return transportationGroupMapper.mapToTransportationGroupResponse(savedGroup);
		}
		throw new ResourceFoundException("Transportation Group Already Exists");
	}

	@Override
	public List<TransportationGroupResponse> getAllTg() {
		return transportationGroupRepository.findAll().stream().sorted(Comparator.comparing(TransportationGroup::getId))
				.map(transportationGroupMapper::mapToTransportationGroupResponse).toList();
	}

	@Override
	public TransportationGroupResponse getTgById(@NonNull Long id) throws ResourceNotFoundException {
		TransportationGroup transportationGroup = this.findTgById(id);
		return transportationGroupMapper.mapToTransportationGroupResponse(transportationGroup);
	}

	@Override
	public List<TransportationGroupResponse> findAllStatusTrue() {
		List<TransportationGroup> transportationGroups = transportationGroupRepository.findAllByTgStatusIsTrue();
		return transportationGroups.stream().sorted(Comparator.comparing(TransportationGroup::getId))
				.map(transportationGroupMapper::mapToTransportationGroupResponse).toList();
	}

	@Override
	public TransportationGroupResponse updateTg(@NonNull Long id,
			TransportationGroupRequest updateTransportationGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateTransportationGroupRequest);
		String tgCode = updateTransportationGroupRequest.getTgCode();
		String tgName = updateTransportationGroupRequest.getTgName();
		TransportationGroup existingTransportationGroup = this.findTgById(id);
		boolean exists = transportationGroupRepository.existsByTgCodeAndIdNotOrTgNameAndIdNot(tgCode, id, tgName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingTransportationGroup.getTgCode().equals(tgCode)) {
				auditFields.add(new AuditFields(null, "Tg Code", existingTransportationGroup.getTgCode(), tgCode));
				existingTransportationGroup.setTgCode(tgCode);
			}
			if (!existingTransportationGroup.getTgName().equals(tgName)) {
				auditFields.add(new AuditFields(null, "Tg Name", existingTransportationGroup.getTgName(), tgName));
				existingTransportationGroup.setTgName(tgName);
			}
			if (!existingTransportationGroup.getTgStatus().equals(updateTransportationGroupRequest.getTgStatus())) {
				auditFields.add(new AuditFields(null, "Tg Status", existingTransportationGroup.getTgStatus(),
						updateTransportationGroupRequest.getTgStatus()));
				existingTransportationGroup.setTgStatus(updateTransportationGroupRequest.getTgStatus());
			}
			if (!existingTransportationGroup.getDynamicFields()
					.equals(updateTransportationGroupRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateTransportationGroupRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingTransportationGroup.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingTransportationGroup.getDynamicFields().put(fieldName, newValue); // Update the dynamic
					}
				}
			}
			existingTransportationGroup.updateAuditHistory(auditFields);
			TransportationGroup updatedGroup = transportationGroupRepository.save(existingTransportationGroup);
			return transportationGroupMapper.mapToTransportationGroupResponse(updatedGroup);
		}
		throw new ResourceFoundException("Transportation Group Already Exists");
	}

	@Override
	public TransportationGroupResponse updateTgStatus(@NonNull Long id) throws ResourceNotFoundException {
		TransportationGroup existingTransportationGroup = this.findTgById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingTransportationGroup.getTgStatus() != null) {
			auditFields.add(new AuditFields(null, "Tg Status", existingTransportationGroup.getTgStatus(),
					!existingTransportationGroup.getTgStatus()));
			existingTransportationGroup.setTgStatus(!existingTransportationGroup.getTgStatus());
		}
		existingTransportationGroup.updateAuditHistory(auditFields);
		transportationGroupRepository.save(existingTransportationGroup);
		return transportationGroupMapper.mapToTransportationGroupResponse(existingTransportationGroup);
	}

	@Override
	public List<TransportationGroupResponse> updateBatchTgStatus(@NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<TransportationGroup> groups = this.findAllTgById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		groups.forEach(existingTransportationGroup -> {
			if (existingTransportationGroup.getTgStatus() != null) {
				auditFields.add(new AuditFields(null, "Tg Status", existingTransportationGroup.getTgStatus(),
						!existingTransportationGroup.getTgStatus()));
				existingTransportationGroup.setTgStatus(!existingTransportationGroup.getTgStatus());
			}
			existingTransportationGroup.updateAuditHistory(auditFields);

		});
		transportationGroupRepository.saveAll(groups);
		return groups.stream().map(transportationGroupMapper::mapToTransportationGroupResponse).toList();
	}

	@Override
	public void deleteTgById(@NonNull Long id) throws ResourceNotFoundException {
		TransportationGroup transportationGroup = this.findTgById(id);
		if (transportationGroup != null) {
			transportationGroupRepository.delete(transportationGroup);
		}
	}

	@Override
	public void deleteBatchTg(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<TransportationGroup> transportationGroups = this.findAllTgById(ids);
		if (!transportationGroups.isEmpty()) {
			transportationGroupRepository.deleteAll(transportationGroups);
		}
	}

	private void validateDynamicFields(TransportationGroup group) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : group.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = TransportationGroup.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private TransportationGroup findTgById(@NonNull Long id) throws ResourceNotFoundException {
		return transportationGroupRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Transportation Group not found with this Id"));
	}

	private List<TransportationGroup> findAllTgById(@NonNull List<Long> ids) throws ResourceNotFoundException {

		List<TransportationGroup> groups = transportationGroupRepository.findAllById(ids);
		// Create a map for faster lookup
		Map<Long, TransportationGroup> groupMap = groups.stream()
				.collect(Collectors.toMap(TransportationGroup::getId, Function.identity()));

		List<Long> missingIds = ids.stream().filter(id -> !groupMap.containsKey(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Transportation Group with IDs " + missingIds + " not found");
		}

		return groups;
	}

}
