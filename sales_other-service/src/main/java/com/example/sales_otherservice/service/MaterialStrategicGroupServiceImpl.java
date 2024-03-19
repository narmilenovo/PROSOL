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
import com.example.sales_otherservice.dto.request.MaterialStrategicGroupRequest;
import com.example.sales_otherservice.dto.response.MaterialStrategicGroupResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.MaterialStrategicGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.MaterialStrategicGroupMapper;
import com.example.sales_otherservice.repository.MaterialStrategicGroupRepository;
import com.example.sales_otherservice.service.interfaces.MaterialStrategicGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialStrategicGroupServiceImpl implements MaterialStrategicGroupService {
	private final MaterialStrategicGroupRepository materialStrategicGroupRepository;
	private final MaterialStrategicGroupMapper strategicGroupMapper;
	private final DynamicClient dynamicClient;

	@Override
	public MaterialStrategicGroupResponse saveMsg(MaterialStrategicGroupRequest materialStrategicGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(materialStrategicGroupRequest);
		String msCode = materialStrategicGroupRequest.getMsCode();
		String msName = materialStrategicGroupRequest.getMsName();
		if (materialStrategicGroupRepository.existsByMsCodeOrMsName(msCode, msName)) {
			throw new ResourceFoundException("Material Strategic Already exists");
		}

		MaterialStrategicGroup strategicGroup = strategicGroupMapper.mapToStrategicGroup(materialStrategicGroupRequest);

		validateDynamicFields(strategicGroup);

		MaterialStrategicGroup savedMaterialStrategicGroup = materialStrategicGroupRepository.save(strategicGroup);
		return strategicGroupMapper.mapToStrategicGroupResponse(savedMaterialStrategicGroup);
	}

	@Override
	public MaterialStrategicGroupResponse getMsgById(@NonNull Long id) throws ResourceNotFoundException {
		MaterialStrategicGroup strategicGroup = this.findMsgById(id);
		return strategicGroupMapper.mapToStrategicGroupResponse(strategicGroup);
	}

	@Override
	public List<MaterialStrategicGroupResponse> getAllMsg() {
		List<MaterialStrategicGroup> strategicGroups = materialStrategicGroupRepository.findAll();
		return strategicGroups.stream().sorted(Comparator.comparing(MaterialStrategicGroup::getId))
				.map(strategicGroupMapper::mapToStrategicGroupResponse).toList();
	}

	@Override
	public List<MaterialStrategicGroupResponse> findAllStatusTrue() {
		List<MaterialStrategicGroup> strategicGroups = materialStrategicGroupRepository.findAllByMsStatusIsTrue();
		return strategicGroups.stream().sorted(Comparator.comparing(MaterialStrategicGroup::getId))
				.map(strategicGroupMapper::mapToStrategicGroupResponse).toList();
	}

	@Override
	public MaterialStrategicGroupResponse updateMsg(@NonNull Long id,
			MaterialStrategicGroupRequest updateMaterialStrategicGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updateMaterialStrategicGroupRequest);
		String msgCode = updateMaterialStrategicGroupRequest.getMsCode();
		String msgName = updateMaterialStrategicGroupRequest.getMsName();
		MaterialStrategicGroup existingStrategicGroup = this.findMsgById(id);
		boolean exists = materialStrategicGroupRepository.existsByMsCodeAndIdNotOrMsNameAndIdNot(msgCode, id, msgName,
				id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingStrategicGroup.getMsCode().equals(msgCode)) {
				auditFields.add(new AuditFields(null, "Msg Code", existingStrategicGroup.getMsCode(), msgCode));
				existingStrategicGroup.setMsCode(msgCode);
			}
			if (!existingStrategicGroup.getMsName().equals(msgName)) {
				auditFields.add(new AuditFields(null, "Msg Name", existingStrategicGroup.getMsName(), msgName));
				existingStrategicGroup.setMsName(msgName);
			}
			if (!existingStrategicGroup.getMsStatus().equals(updateMaterialStrategicGroupRequest.getMsStatus())) {
				auditFields.add(new AuditFields(null, "Msg Status", existingStrategicGroup.getMsStatus(),
						updateMaterialStrategicGroupRequest.getMsStatus()));
				existingStrategicGroup.setMsStatus(updateMaterialStrategicGroupRequest.getMsStatus());
			}
			if (!existingStrategicGroup.getDynamicFields()
					.equals(updateMaterialStrategicGroupRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateMaterialStrategicGroupRequest.getDynamicFields()
						.entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingStrategicGroup.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingStrategicGroup.getDynamicFields().put(fieldName, newValue);
					}
				}
			}
			existingStrategicGroup.updateAuditHistory(auditFields);
			MaterialStrategicGroup updatedStrategicGroup = materialStrategicGroupRepository
					.save(existingStrategicGroup);
			return strategicGroupMapper.mapToStrategicGroupResponse(updatedStrategicGroup);
		}
		throw new ResourceFoundException("Material Strategic Already exists");
	}

	@Override
	public MaterialStrategicGroupResponse updateMsgStatus(@NonNull Long id) throws ResourceNotFoundException {
		MaterialStrategicGroup existingStrategicGroup = this.findMsgById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingStrategicGroup.getMsStatus() != null) {
			auditFields.add(new AuditFields(null, "Msg Status", existingStrategicGroup.getMsStatus(),
					!existingStrategicGroup.getMsStatus()));
			existingStrategicGroup.setMsStatus(!existingStrategicGroup.getMsStatus());
		}
		existingStrategicGroup.updateAuditHistory(auditFields);
		MaterialStrategicGroup updatedStrategicGroup = materialStrategicGroupRepository.save(existingStrategicGroup);
		return strategicGroupMapper.mapToStrategicGroupResponse(updatedStrategicGroup);
	}

	@Override
	public List<MaterialStrategicGroupResponse> updateBatchMsgStatus(@NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<MaterialStrategicGroup> strategicGroups = this.findAllMsgById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		strategicGroups.forEach(existingStrategicGroup -> {
			if (existingStrategicGroup.getMsStatus() != null) {
				auditFields.add(new AuditFields(null, "Msg Status", existingStrategicGroup.getMsStatus(),
						!existingStrategicGroup.getMsStatus()));
				existingStrategicGroup.setMsStatus(!existingStrategicGroup.getMsStatus());
			}
			existingStrategicGroup.updateAuditHistory(auditFields);

		});
		materialStrategicGroupRepository.saveAll(strategicGroups);
		return strategicGroups.stream().sorted(Comparator.comparing(MaterialStrategicGroup::getId))
				.map(strategicGroupMapper::mapToStrategicGroupResponse).toList();
	}

	@Override
	public void deleteMsgById(@NonNull Long id) throws ResourceNotFoundException {
		MaterialStrategicGroup strategicGroup = this.findMsgById(id);
		if (strategicGroup != null) {
			materialStrategicGroupRepository.delete(strategicGroup);
		}
	}

	@Override
	public void deleteBatchMsg(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<MaterialStrategicGroup> strategicGroups = this.findAllMsgById(ids);
		if (!strategicGroups.isEmpty()) {
			materialStrategicGroupRepository.deleteAll(strategicGroups);
		}

	}

	private void validateDynamicFields(MaterialStrategicGroup strategicGroup) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : strategicGroup.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = MaterialStrategicGroup.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private MaterialStrategicGroup findMsgById(@NonNull Long id) throws ResourceNotFoundException {
		return materialStrategicGroupRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Material Strategic Group not found with this Id"));
	}

	private List<MaterialStrategicGroup> findAllMsgById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<MaterialStrategicGroup> strategicGroups = materialStrategicGroupRepository.findAllById(ids);

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Material Strategic Group with IDs " + missingIds + " not found.");
		}
		return strategicGroups;
	}

}
