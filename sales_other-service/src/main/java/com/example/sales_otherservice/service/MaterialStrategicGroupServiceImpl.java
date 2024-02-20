package com.example.sales_otherservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.MaterialStrategicGroupRequest;
import com.example.sales_otherservice.dto.response.MaterialStrategicGroupResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.MaterialStrategicGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.MaterialStrategicGroupRepository;
import com.example.sales_otherservice.service.interfaces.MaterialStrategicGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialStrategicGroupServiceImpl implements MaterialStrategicGroupService {
	private final MaterialStrategicGroupRepository materialStrategicGroupRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public MaterialStrategicGroupResponse saveMsg(MaterialStrategicGroupRequest materialStrategicGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(materialStrategicGroupRequest);
		String msCode = materialStrategicGroupRequest.getMsCode();
		String msName = materialStrategicGroupRequest.getMsName();
		boolean exists = materialStrategicGroupRepository.existsByMsCodeOrMsName(msCode, msName);
		if (!exists) {

			MaterialStrategicGroup strategicGroup = modelMapper.map(materialStrategicGroupRequest,
					MaterialStrategicGroup.class);
			for (Map.Entry<String, Object> entryField : strategicGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = MaterialStrategicGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			MaterialStrategicGroup savedMaterialStrategicGroup = materialStrategicGroupRepository.save(strategicGroup);
			return mapToStrategicGroupResponse(savedMaterialStrategicGroup);
		}
		throw new ResourceFoundException("Material Strategic Already exists");
	}

	@Override
	public MaterialStrategicGroupResponse getMsgById(Long id) throws ResourceNotFoundException {
		MaterialStrategicGroup strategicGroup = this.findMsgById(id);
		return mapToStrategicGroupResponse(strategicGroup);
	}

	@Override
	public List<MaterialStrategicGroupResponse> getAllMsg() {
		List<MaterialStrategicGroup> strategicGroups = materialStrategicGroupRepository.findAll();
		return strategicGroups.stream().sorted(Comparator.comparing(MaterialStrategicGroup::getId))
				.map(this::mapToStrategicGroupResponse).toList();
	}

	@Override
	public List<MaterialStrategicGroupResponse> findAllStatusTrue() {
		List<MaterialStrategicGroup> strategicGroups = materialStrategicGroupRepository.findAllByMsStatusIsTrue();
		return strategicGroups.stream().sorted(Comparator.comparing(MaterialStrategicGroup::getId))
				.map(this::mapToStrategicGroupResponse).toList();
	}

	@Override
	public MaterialStrategicGroupResponse updateMsg(Long id,
			MaterialStrategicGroupRequest updateMaterialStrategicGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
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
			return mapToStrategicGroupResponse(updatedStrategicGroup);
		}
		throw new ResourceFoundException("Material Strategic Already exists");
	}

	@Override
	public MaterialStrategicGroupResponse updateMsgStatus(Long id) throws ResourceNotFoundException {
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
		return mapToStrategicGroupResponse(updatedStrategicGroup);
	}

	@Override
	public List<MaterialStrategicGroupResponse> updateBatchMsgStatus(List<Long> ids) throws ResourceNotFoundException {
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
				.map(this::mapToStrategicGroupResponse).toList();
	}

	@Override
	public void deleteMsgById(Long id) throws ResourceNotFoundException {
		MaterialStrategicGroup strategicGroup = this.findMsgById(id);
		materialStrategicGroupRepository.deleteById(strategicGroup.getId());
	}

	@Override
	public void deleteBatchMsg(List<Long> ids) throws ResourceNotFoundException {
		this.findAllMsgById(ids);
		materialStrategicGroupRepository.deleteAllByIdInBatch(ids);

	}

	private MaterialStrategicGroupResponse mapToStrategicGroupResponse(MaterialStrategicGroup materialStrategicGroup) {
		return modelMapper.map(materialStrategicGroup, MaterialStrategicGroupResponse.class);
	}

	private MaterialStrategicGroup findMsgById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<MaterialStrategicGroup> strategicGroup = materialStrategicGroupRepository.findById(id);
		if (strategicGroup.isEmpty()) {
			throw new ResourceNotFoundException("Material Strategic Group not found with this Id");
		}
		return strategicGroup.get();
	}

	private List<MaterialStrategicGroup> findAllMsgById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<MaterialStrategicGroup> strategicGroups = materialStrategicGroupRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> strategicGroups.stream().noneMatch(entity -> entity.getId().equals(id))).toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Material Strategic Group with IDs " + missingIds + " not found.");
		}
		return strategicGroups;
	}

}
