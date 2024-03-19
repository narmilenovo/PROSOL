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
import com.example.sales_otherservice.dto.request.PurchasingValueKeyRequest;
import com.example.sales_otherservice.dto.response.PurchasingValueKeyResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.PurchasingValueKey;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.PurchasingValueKeyMapper;
import com.example.sales_otherservice.repository.PurchasingValueKeyRepository;
import com.example.sales_otherservice.service.interfaces.PurchasingValueKeyService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchasingValueKeyServiceImpl implements PurchasingValueKeyService {
	private final PurchasingValueKeyRepository purchasingValueKeyRepository;
	private final PurchasingValueKeyMapper valueKeyMapper;
	private final DynamicClient dynamicClient;

	@Override
	public PurchasingValueKeyResponse savePvk(PurchasingValueKeyRequest purchasingValueKeyRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(purchasingValueKeyRequest);
		String pvkCode = purchasingValueKeyRequest.getPvkCode();
		String pvkName = purchasingValueKeyRequest.getPvkName();
		if (purchasingValueKeyRepository.existsByPvkCodeOrPvkName(pvkCode, pvkName)) {
			throw new ResourceFoundException("Purchasing Value Key Already exist");
		}

		PurchasingValueKey valueKey = valueKeyMapper.mapToPurchasingValueKey(purchasingValueKeyRequest);

		validateDynamicFields(valueKey);

		PurchasingValueKey savedValueKey = purchasingValueKeyRepository.save(valueKey);
		return valueKeyMapper.mapToPurchasingValueKeyResponse(savedValueKey);
	}

	@Override
	public PurchasingValueKeyResponse getPvkById(@NonNull Long id) throws ResourceNotFoundException {
		PurchasingValueKey valueKey = this.findPvkById(id);
		return valueKeyMapper.mapToPurchasingValueKeyResponse(valueKey);
	}

	@Override
	public List<PurchasingValueKeyResponse> getAllPvk() {
		List<PurchasingValueKey> purchasingValueKeys = purchasingValueKeyRepository.findAll();
		return purchasingValueKeys.stream().sorted(Comparator.comparing(PurchasingValueKey::getId))
				.map(valueKeyMapper::mapToPurchasingValueKeyResponse).toList();
	}

	@Override
	public List<PurchasingValueKeyResponse> findAllStatusTrue() {
		List<PurchasingValueKey> purchasingValueKeys = purchasingValueKeyRepository.findAllByPvkStatusIsTrue();
		return purchasingValueKeys.stream().sorted(Comparator.comparing(PurchasingValueKey::getId))
				.map(valueKeyMapper::mapToPurchasingValueKeyResponse).toList();
	}

	@Override
	public PurchasingValueKeyResponse updatePvk(@NonNull Long id,
			PurchasingValueKeyRequest updatePurchasingValueKeyRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updatePurchasingValueKeyRequest);
		String pvkCode = updatePurchasingValueKeyRequest.getPvkCode();
		String pvkName = updatePurchasingValueKeyRequest.getPvkName();
		PurchasingValueKey existingValueKey = this.findPvkById(id);
		boolean exists = purchasingValueKeyRepository.existsByPvkCodeAndIdNotOrPvkNameAndIdNot(pvkCode, id, pvkName,
				id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingValueKey.getPvkCode().equals(pvkCode)) {
				auditFields.add(new AuditFields(null, "Pvk Code", existingValueKey.getPvkCode(), pvkCode));
				existingValueKey.setPvkCode(pvkCode);
			}
			if (!existingValueKey.getPvkName().equals(pvkName)) {
				auditFields.add(new AuditFields(null, "Pvk Name", existingValueKey.getPvkName(), pvkName));
				existingValueKey.setPvkName(pvkName);
			}
			if (!existingValueKey.getPvkStatus().equals(updatePurchasingValueKeyRequest.getPvkStatus())) {
				auditFields.add(new AuditFields(null, "Pvk Status", existingValueKey.getPvkStatus(),
						updatePurchasingValueKeyRequest.getPvkStatus()));
				existingValueKey.setPvkStatus(updatePurchasingValueKeyRequest.getPvkStatus());
			}
			if (!existingValueKey.getDynamicFields().equals(updatePurchasingValueKeyRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updatePurchasingValueKeyRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingValueKey.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingValueKey.getDynamicFields().put(fieldName, newValue);
					}
				}
			}
			existingValueKey.updateAuditHistory(auditFields);
			PurchasingValueKey updatedValueKey = purchasingValueKeyRepository.save(existingValueKey);
			return valueKeyMapper.mapToPurchasingValueKeyResponse(updatedValueKey);
		}
		throw new ResourceFoundException("Purchasing Value Key Already exist");
	}

	@Override
	public PurchasingValueKeyResponse updatePvkStatus(@NonNull Long id) throws ResourceNotFoundException {
		PurchasingValueKey existingValueKey = this.findPvkById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingValueKey.getPvkStatus() != null) {
			auditFields.add(new AuditFields(null, "Pvk Status", existingValueKey.getPvkStatus(),
					!existingValueKey.getPvkStatus()));
			existingValueKey.setPvkStatus(!existingValueKey.getPvkStatus());
		}
		existingValueKey.updateAuditHistory(auditFields);
		purchasingValueKeyRepository.save(existingValueKey);
		return valueKeyMapper.mapToPurchasingValueKeyResponse(existingValueKey);
	}

	@Override
	public List<PurchasingValueKeyResponse> updateBatchPvkStatus(@NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<PurchasingValueKey> valueKeys = this.findAllPvkById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		valueKeys.forEach(existingValueKey -> {
			if (existingValueKey.getPvkStatus() != null) {
				auditFields.add(new AuditFields(null, "Pvk Status", existingValueKey.getPvkStatus(),
						!existingValueKey.getPvkStatus()));
				existingValueKey.setPvkStatus(!existingValueKey.getPvkStatus());
			}
			existingValueKey.updateAuditHistory(auditFields);

		});
		purchasingValueKeyRepository.saveAll(valueKeys);
		return valueKeys.stream().map(valueKeyMapper::mapToPurchasingValueKeyResponse).toList();
	}

	@Override
	public void deletePvkById(@NonNull Long id) throws ResourceNotFoundException {
		PurchasingValueKey valueKey = this.findPvkById(id);
		if (valueKey != null) {
			purchasingValueKeyRepository.delete(valueKey);
		}
	}

	@Override
	public void deleteBatchPvk(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<PurchasingValueKey> valueKeys = this.findAllPvkById(ids);
		if (!valueKeys.isEmpty()) {
			purchasingValueKeyRepository.deleteAll(valueKeys);
		}
	}

	private void validateDynamicFields(PurchasingValueKey valueKey) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : valueKey.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = PurchasingValueKey.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private PurchasingValueKey findPvkById(@NonNull Long id) throws ResourceNotFoundException {
		return purchasingValueKeyRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Purchasing Value Key not found with this Id"));
	}

	private List<PurchasingValueKey> findAllPvkById(@NonNull List<Long> ids) throws ResourceNotFoundException {

		List<PurchasingValueKey> purchasingValueKeys = purchasingValueKeyRepository.findAllById(ids);

		Map<Long, PurchasingValueKey> purchasingValueKeyMap = purchasingValueKeys.stream()
				.collect(Collectors.toMap(PurchasingValueKey::getId, Function.identity()));

		List<Long> missingIds = ids.stream().filter(id -> !purchasingValueKeyMap.containsKey(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Purchasing Value Key with IDs " + missingIds + " not found");
		}

		return purchasingValueKeys;
	}

}
