package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.TransportationGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.TransportationGroupRepository;
import com.example.sales_otherservice.service.interfaces.TransportationGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransportationGroupServiceImpl implements TransportationGroupService {
	private final TransportationGroupRepository transportationGroupRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public TransportationGroupResponse saveTg(TransportationGroupRequest transportationGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(transportationGroupRequest);
		String tgCode = transportationGroupRequest.getTgCode();
		String tgName = transportationGroupRequest.getTgName();
		boolean exists = transportationGroupRepository.existsByTgCodeOrTgName(tgCode, tgName);
		if (!exists) {

			TransportationGroup group = modelMapper.map(transportationGroupRequest, TransportationGroup.class);
			for (Map.Entry<String, Object> entryField : group.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = TransportationGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			TransportationGroup savedGroup = transportationGroupRepository.save(group);
			return mapToTransportationGroupResponse(savedGroup);
		}
		throw new ResourceFoundException("Transportation Group Already Exists");
	}

	@Override
	public List<TransportationGroupResponse> getAllTg() {
		List<TransportationGroup> transportationGroups = transportationGroupRepository.findAll();
		return transportationGroups.stream().sorted(Comparator.comparing(TransportationGroup::getId))
				.map(this::mapToTransportationGroupResponse).toList();
	}

	@Override
	public TransportationGroupResponse getTgById(Long id) throws ResourceNotFoundException {
		TransportationGroup transportationGroup = this.findTgById(id);
		return mapToTransportationGroupResponse(transportationGroup);
	}

	@Override
	public List<TransportationGroupResponse> findAllStatusTrue() {
		List<TransportationGroup> transportationGroups = transportationGroupRepository.findAllByTgStatusIsTrue();
		return transportationGroups.stream().sorted(Comparator.comparing(TransportationGroup::getId))
				.map(this::mapToTransportationGroupResponse).toList();
	}

	@Override
	public TransportationGroupResponse updateTg(Long id, TransportationGroupRequest updateTransportationGroupRequest)
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
			return mapToTransportationGroupResponse(updatedGroup);
		}
		throw new ResourceFoundException("Transportation Group Already Exists");
	}

	@Override
	public TransportationGroupResponse updateTgStatus(Long id) throws ResourceNotFoundException {
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
		return mapToTransportationGroupResponse(existingTransportationGroup);
	}

	@Override
	public List<TransportationGroupResponse> updateBatchTgStatus(List<Long> ids) throws ResourceNotFoundException {
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
		return groups.stream().map(this::mapToTransportationGroupResponse).toList();
	}

	@Override
	public void deleteTgById(Long id) throws ResourceNotFoundException {
		TransportationGroup transportationGroup = this.findTgById(id);
		transportationGroupRepository.deleteById(transportationGroup.getId());
	}

	@Override
	public void deleteBatchTg(List<Long> ids) throws ResourceNotFoundException {
		this.findAllTgById(ids);
		transportationGroupRepository.deleteAllByIdInBatch(ids);
	}

	private TransportationGroupResponse mapToTransportationGroupResponse(TransportationGroup taxClassificationType) {
		return modelMapper.map(taxClassificationType, TransportationGroupResponse.class);
	}

	private TransportationGroup findTgById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<TransportationGroup> transportationGroup = transportationGroupRepository.findById(id);
		if (transportationGroup.isEmpty()) {
			throw new ResourceNotFoundException("Transportation Group not found with this Id");
		}
		return transportationGroup.get();
	}

	private List<TransportationGroup> findAllTgById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<TransportationGroup> groups = transportationGroupRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> groups.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Transportation Group with IDs " + missingIds + " not found");
		}
		return groups;
	}

}
