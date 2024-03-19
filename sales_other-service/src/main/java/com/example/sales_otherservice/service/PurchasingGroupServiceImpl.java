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
import com.example.sales_otherservice.dto.request.PurchasingGroupRequest;
import com.example.sales_otherservice.dto.response.PurchasingGroupResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.PurchasingGroup;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.PurchasingGroupMapper;
import com.example.sales_otherservice.repository.PurchasingGroupRepository;
import com.example.sales_otherservice.service.interfaces.PurchasingGroupService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchasingGroupServiceImpl implements PurchasingGroupService {
	private final PurchasingGroupRepository purchasingGroupRepository;
	private final PurchasingGroupMapper purchasingGroupMapper;
	private final DynamicClient dynamicClient;

	@Override
	public PurchasingGroupResponse savePg(PurchasingGroupRequest purchasingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(purchasingGroupRequest);
		String pgCode = purchasingGroupRequest.getPgCode();
		String pgName = purchasingGroupRequest.getPgName();
		if (purchasingGroupRepository.existsByPgCodeOrPgName(pgCode, pgName)) {
			throw new ResourceFoundException("Purchasing Group Already exist");
		}

		PurchasingGroup purchasingGroup = purchasingGroupMapper.mapToPurchasingGroup(purchasingGroupRequest);

		validateDynamicFields(purchasingGroup);

		PurchasingGroup savedPurchasingGroup = purchasingGroupRepository.save(purchasingGroup);
		return purchasingGroupMapper.mapToPurchasingGroupResponse(savedPurchasingGroup);
	}

	@Override
	public PurchasingGroupResponse getPgById(@NonNull Long id) throws ResourceNotFoundException {
		PurchasingGroup purchasingGroup = this.findPgById(id);
		return purchasingGroupMapper.mapToPurchasingGroupResponse(purchasingGroup);
	}

	@Override
	public List<PurchasingGroupResponse> getAllPg() {
		return purchasingGroupRepository.findAll().stream().sorted(Comparator.comparing(PurchasingGroup::getId))
				.map(purchasingGroupMapper::mapToPurchasingGroupResponse).toList();

	}

	@Override
	public List<PurchasingGroupResponse> findAllStatusTrue() {
		return purchasingGroupRepository.findAllByPgStatusIsTrue().stream()
				.sorted(Comparator.comparing(PurchasingGroup::getId))
				.map(purchasingGroupMapper::mapToPurchasingGroupResponse).toList();
	}

	@Override
	public PurchasingGroupResponse updatePg(@NonNull Long id, PurchasingGroupRequest updatePurchasingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updatePurchasingGroupRequest);
		String pgCode = updatePurchasingGroupRequest.getPgCode();
		String pgName = updatePurchasingGroupRequest.getPgName();
		PurchasingGroup existingPurchasingGroup = this.findPgById(id);
		boolean exists = purchasingGroupRepository.existsByPgCodeAndIdNotOrPgNameAndIdNot(pgCode, id, pgName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingPurchasingGroup.getPgCode().equals(pgCode)) {
				auditFields.add(new AuditFields(null, "Pg Code", existingPurchasingGroup.getPgCode(), pgCode));
				existingPurchasingGroup.setPgCode(pgCode);
			}
			if (!existingPurchasingGroup.getPgName().equals(pgName)) {
				auditFields.add(new AuditFields(null, "Pg Name", existingPurchasingGroup.getPgName(), pgName));
				existingPurchasingGroup.setPgName(pgName);
			}
			if (!existingPurchasingGroup.getPgStatus().equals(updatePurchasingGroupRequest.getPgStatus())) {
				auditFields.add(new AuditFields(null, "Pg Status", existingPurchasingGroup.getPgStatus(),
						updatePurchasingGroupRequest.getPgStatus()));
				existingPurchasingGroup.setPgStatus(updatePurchasingGroupRequest.getPgStatus());
			}
			if (!existingPurchasingGroup.getDynamicFields().equals(updatePurchasingGroupRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updatePurchasingGroupRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingPurchasingGroup.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingPurchasingGroup.getDynamicFields().put(fieldName, newValue);
					}
				}
			}
			existingPurchasingGroup.updateAuditHistory(auditFields);
			PurchasingGroup updatedPurchasingGroup = purchasingGroupRepository.save(existingPurchasingGroup);
			return purchasingGroupMapper.mapToPurchasingGroupResponse(updatedPurchasingGroup);
		}
		throw new ResourceFoundException("Purchasing Group Already exist");
	}

	@Override
	public PurchasingGroupResponse updatePgStatus(@NonNull Long id) throws ResourceNotFoundException {
		PurchasingGroup existingPurchasingGroup = this.findPgById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingPurchasingGroup.getPgStatus() != null) {
			auditFields.add(new AuditFields(null, "Pg Status", existingPurchasingGroup.getPgStatus(),
					!existingPurchasingGroup.getPgStatus()));
			existingPurchasingGroup.setPgStatus(!existingPurchasingGroup.getPgStatus());
		}
		existingPurchasingGroup.updateAuditHistory(auditFields);
		purchasingGroupRepository.save(existingPurchasingGroup);
		return purchasingGroupMapper.mapToPurchasingGroupResponse(existingPurchasingGroup);
	}

	@Override
	public List<PurchasingGroupResponse> updateBatchPgStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<PurchasingGroup> purchasingGroups = this.findAllPdById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		purchasingGroups.forEach(existingPurchasingGroup -> {
			if (existingPurchasingGroup.getPgStatus() != null) {
				auditFields.add(new AuditFields(null, "Pg Status", existingPurchasingGroup.getPgStatus(),
						!existingPurchasingGroup.getPgStatus()));
				existingPurchasingGroup.setPgStatus(!existingPurchasingGroup.getPgStatus());
			}
			existingPurchasingGroup.updateAuditHistory(auditFields);
		});
		purchasingGroupRepository.saveAll(purchasingGroups);
		return purchasingGroups.stream().map(purchasingGroupMapper::mapToPurchasingGroupResponse).toList();
	}

	@Override
	public void deletePgById(@NonNull Long id) throws ResourceNotFoundException {
		PurchasingGroup purchasingGroup = this.findPgById(id);
		if (purchasingGroup != null) {
			purchasingGroupRepository.delete(purchasingGroup);
		}
	}

	@Override
	public void deleteBatchPg(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<PurchasingGroup> purchasingGroups = this.findAllPdById(ids);
		if (!purchasingGroups.isEmpty()) {
			purchasingGroupRepository.deleteAll(purchasingGroups);
		}
	}

	private void validateDynamicFields(PurchasingGroup purchasingGroup) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : purchasingGroup.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = PurchasingGroup.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private PurchasingGroup findPgById(@NonNull Long id) throws ResourceNotFoundException {
		return purchasingGroupRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Purchasing Group not found with this Id"));
	}

	private List<PurchasingGroup> findAllPdById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<PurchasingGroup> purchasingGroups = purchasingGroupRepository.findAllById(ids);

		Map<Long, PurchasingGroup> purchasingGroupMap = purchasingGroups.stream()
				.collect(Collectors.toMap(PurchasingGroup::getId, Function.identity()));

		List<Long> missingIds = ids.stream().filter(id -> !purchasingGroupMap.containsKey(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Purchasing Group with IDs " + missingIds + " not found");
		}

		return purchasingGroups;
	}

}
