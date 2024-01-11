package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.DynamicClient;
import com.example.sales_otherservice.dto.request.MaterialStrategicGroupRequest;
import com.example.sales_otherservice.dto.response.MaterialStrategicGroupResponse;
import com.example.sales_otherservice.entity.MaterialStrategicGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.MaterialStrategicGroupRepository;
import com.example.sales_otherservice.service.interfaces.MaterialStrategicGroupService;

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
	public List<MaterialStrategicGroupResponse> getAllMsg() {
		List<MaterialStrategicGroup> strategicGroups = materialStrategicGroupRepository.findAll();
		return strategicGroups.stream().sorted(Comparator.comparing(MaterialStrategicGroup::getId))
				.map(this::mapToStrategicGroupResponse).toList();
	}

	@Override
	public MaterialStrategicGroupResponse getMsgById(Long id) throws ResourceNotFoundException {
		MaterialStrategicGroup strategicGroup = this.findMsgById(id);
		return mapToStrategicGroupResponse(strategicGroup);
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
		String msgCode = updateMaterialStrategicGroupRequest.getMsCode();
		String msgName = updateMaterialStrategicGroupRequest.getMsName();
		MaterialStrategicGroup existingStrategicGroup = this.findMsgById(id);
		boolean exists = materialStrategicGroupRepository.existsByMsCodeAndIdNotOrMsNameAndIdNot(msgCode, id, msgName,
				id);
		if (!exists) {
			modelMapper.map(updateMaterialStrategicGroupRequest, existingStrategicGroup);
			for (Map.Entry<String, Object> entryField : existingStrategicGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = MaterialStrategicGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			MaterialStrategicGroup updatedStrategicGroup = materialStrategicGroupRepository
					.save(existingStrategicGroup);
			return mapToStrategicGroupResponse(updatedStrategicGroup);
		}
		throw new ResourceFoundException("Material Strategic Already exists");
	}

	@Override
	public void deleteMsgById(Long id) throws ResourceNotFoundException {
		MaterialStrategicGroup strategicGroup = this.findMsgById(id);
		materialStrategicGroupRepository.deleteById(strategicGroup.getId());
	}

	@Override
	public void deleteBatchMsg(List<Long> ids) {
		materialStrategicGroupRepository.deleteAllByIdInBatch(ids);

	}

	private MaterialStrategicGroupResponse mapToStrategicGroupResponse(MaterialStrategicGroup materialStrategicGroup) {
		return modelMapper.map(materialStrategicGroup, MaterialStrategicGroupResponse.class);
	}

	private MaterialStrategicGroup findMsgById(Long id) throws ResourceNotFoundException {
		Optional<MaterialStrategicGroup> strategicGroup = materialStrategicGroupRepository.findById(id);
		if (strategicGroup.isEmpty()) {
			throw new ResourceNotFoundException("Material Strategic Group not found with this Id");
		}
		return strategicGroup.get();
	}

}
