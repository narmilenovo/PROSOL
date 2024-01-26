package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.entity.PurchasingGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.PurchasingGroupRepository;
import com.example.sales_otherservice.service.interfaces.PurchasingGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchasingGroupServiceImpl implements PurchasingGroupService {
	private final PurchasingGroupRepository purchasingGroupRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public PurchasingGroupResponse savePg(PurchasingGroupRequest purchasingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String pgCode = purchasingGroupRequest.getPgCode();
		String pgName = purchasingGroupRequest.getPgName();
		boolean exists = purchasingGroupRepository.existsByPgCodeOrPgName(pgCode, pgName);
		if (!exists) {

			PurchasingGroup purchasingGroup = modelMapper.map(purchasingGroupRequest, PurchasingGroup.class);
			for (Map.Entry<String, Object> entryField : purchasingGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = PurchasingGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			PurchasingGroup savedPurchasingGroup = purchasingGroupRepository.save(purchasingGroup);
			return mapToPurchasingGroupResponse(savedPurchasingGroup);
		}
		throw new ResourceFoundException("Purchasing Group Already exist");
	}

	@Override
	public PurchasingGroupResponse getPgById(Long id) throws ResourceNotFoundException {
		PurchasingGroup purchasingGroup = this.findPgById(id);
		return mapToPurchasingGroupResponse(purchasingGroup);
	}

	@Override
	public List<PurchasingGroupResponse> getAllPg() {
		List<PurchasingGroup> purchasingGroups = purchasingGroupRepository.findAll();
		return purchasingGroups.stream().sorted(Comparator.comparing(PurchasingGroup::getId))
				.map(this::mapToPurchasingGroupResponse).toList();

	}

	@Override
	public List<PurchasingGroupResponse> findAllStatusTrue() {
		List<PurchasingGroup> purchasingGroups = purchasingGroupRepository.findAllByPgStatusIsTrue();
		return purchasingGroups.stream().sorted(Comparator.comparing(PurchasingGroup::getId))
				.map(this::mapToPurchasingGroupResponse).toList();
	}

	@Override
	public PurchasingGroupResponse updatePg(Long id, PurchasingGroupRequest updatePurchasingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		String pgCode = updatePurchasingGroupRequest.getPgCode();
		String pgName = updatePurchasingGroupRequest.getPgName();
		PurchasingGroup existingPurchasingGroup = this.findPgById(id);
		boolean exists = purchasingGroupRepository.existsByPgCodeAndIdNotOrPgNameAndIdNot(pgCode, id, pgName, id);
		if (!exists) {
			modelMapper.map(updatePurchasingGroupRequest, existingPurchasingGroup);
			for (Map.Entry<String, Object> entryField : existingPurchasingGroup.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = PurchasingGroup.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			PurchasingGroup updatedPurchasingGroup = purchasingGroupRepository.save(existingPurchasingGroup);
			return mapToPurchasingGroupResponse(updatedPurchasingGroup);
		}
		throw new ResourceFoundException("Purchasing Group Already exist");
	}

	@Override
	public PurchasingGroupResponse updatePgStatus(Long id) throws ResourceNotFoundException {
		PurchasingGroup purchasingGroup = this.findPgById(id);
		purchasingGroup.setPgStatus(!purchasingGroup.getPgStatus());
		purchasingGroupRepository.save(purchasingGroup);
		return mapToPurchasingGroupResponse(purchasingGroup);
	}

	@Override
	public List<PurchasingGroupResponse> updateBatchPgStatus(List<Long> ids) throws ResourceNotFoundException {
		List<PurchasingGroup> purchasingGroups = this.findAllPdById(ids);
		purchasingGroups.forEach(purchasingGroup -> purchasingGroup.setPgStatus(!purchasingGroup.getPgStatus()));
		purchasingGroupRepository.saveAll(purchasingGroups);
		return purchasingGroups.stream().map(this::mapToPurchasingGroupResponse).toList();
	}

	@Override
	public void deletePgById(Long id) throws ResourceNotFoundException {
		PurchasingGroup purchasingGroup = this.findPgById(id);
		purchasingGroupRepository.deleteById(purchasingGroup.getId());
	}

	@Override
	public void deleteBatchPg(List<Long> ids) throws ResourceNotFoundException {
		this.findAllPdById(ids);
		purchasingGroupRepository.deleteAllByIdInBatch(ids);
	}

	private PurchasingGroupResponse mapToPurchasingGroupResponse(PurchasingGroup purchasingGroup) {
		return modelMapper.map(purchasingGroup, PurchasingGroupResponse.class);
	}

	private PurchasingGroup findPgById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<PurchasingGroup> purchasingGroup = purchasingGroupRepository.findById(id);
		if (purchasingGroup.isEmpty()) {
			throw new ResourceNotFoundException("Purchasing Group not found with this Id");
		}
		return purchasingGroup.get();
	}

	private List<PurchasingGroup> findAllPdById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<PurchasingGroup> purchasingGroups = purchasingGroupRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> purchasingGroups.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Purchasing Group with IDs " + missingIds + " not found");
		}
		return purchasingGroups;
	}

}
