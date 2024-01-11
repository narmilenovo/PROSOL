package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.DynamicClient;
import com.example.sales_otherservice.dto.request.TransportationGroupRequest;
import com.example.sales_otherservice.dto.response.TransportationGroupResponse;
import com.example.sales_otherservice.entity.TransportationGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.TransportationGroupRepository;
import com.example.sales_otherservice.service.interfaces.TransportationGroupService;

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
		String tgCode = updateTransportationGroupRequest.getTgCode();
		String tgName = updateTransportationGroupRequest.getTgName();
		TransportationGroup existingTransportationGroup = this.findTgById(id);
		boolean exists = transportationGroupRepository.existsByTgCodeAndIdNotOrTgNameAndIdNot(tgCode, id, tgName, id);
		if (!exists) {
			modelMapper.map(updateTransportationGroupRequest, existingTransportationGroup);
			for (Map.Entry<String, Object> entryField : existingTransportationGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = TransportationGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			TransportationGroup updatedGroup = transportationGroupRepository.save(existingTransportationGroup);
			return mapToTransportationGroupResponse(updatedGroup);
		}
		throw new ResourceFoundException("Transportation Group Already Exists");
	}

	@Override
	public void deleteTgById(Long id) throws ResourceNotFoundException {
		TransportationGroup transportationGroup = this.findTgById(id);
		transportationGroupRepository.deleteById(transportationGroup.getId());
	}

	@Override
	public void deleteBatchTg(List<Long> ids) {
		transportationGroupRepository.deleteAllByIdInBatch(ids);
	}

	private TransportationGroupResponse mapToTransportationGroupResponse(TransportationGroup taxClassificationType) {
		return modelMapper.map(taxClassificationType, TransportationGroupResponse.class);
	}

	private TransportationGroup findTgById(Long id) throws ResourceNotFoundException {
		Optional<TransportationGroup> transportationGroup = transportationGroupRepository.findById(id);
		if (transportationGroup.isEmpty()) {
			throw new ResourceNotFoundException("Transportation Group not found with this Id");
		}
		return transportationGroup.get();
	}

}
