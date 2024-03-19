package com.example.generalservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.SalesUnitRequest;
import com.example.generalservice.dto.response.SalesUnitResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.SalesUnit;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.mapping.SalesUnitMapper;
import com.example.generalservice.repository.SalesUnitRepository;
import com.example.generalservice.service.interfaces.SalesUnitService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesUnitServiceImpl implements SalesUnitService {
	private final SalesUnitRepository salesUnitRepository;
	private final SalesUnitMapper salesUnitMapper;
	private final DynamicClient dynamicClient;

	@Override
	public SalesUnitResponse saveSalesUnit(SalesUnitRequest salesUnitRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(salesUnitRequest);
		String salesCode = salesUnitRequest.getSalesCode();
		String salesName = salesUnitRequest.getSalesName();
		if (salesUnitRepository.existsBySalesCodeOrSalesName(salesCode, salesName)) {
			throw new ResourceFoundException("Sales Unit already exists");
		}
		SalesUnit salesUnit = salesUnitMapper.mapToSalesUnit(salesUnitRequest);
		validateDynamicFields(salesUnit);
		SalesUnit savedSalesUnit = salesUnitRepository.save(salesUnit);
		return salesUnitMapper.mapToSalesUnitResponse(savedSalesUnit);
	}

	@Override
	public SalesUnitResponse getSalesUnitById(@NonNull Long id) throws ResourceNotFoundException {
		SalesUnit salesUnit = this.findSalesUnitById(id);
		return salesUnitMapper.mapToSalesUnitResponse(salesUnit);
	}

	@Override
	public List<SalesUnitResponse> getAllSalesUnit() {
		return salesUnitRepository.findAll().stream().sorted(Comparator.comparing(SalesUnit::getId))
				.map(salesUnitMapper::mapToSalesUnitResponse).toList();
	}

	@Override
	public List<SalesUnitResponse> findAllStatusTrue() {
		return salesUnitRepository.findAllBySalesStatusIsTrue().stream().sorted(Comparator.comparing(SalesUnit::getId))
				.map(salesUnitMapper::mapToSalesUnitResponse).toList();
	}

	@Override
	public SalesUnitResponse updateSalesUnit(@NonNull Long id, SalesUnitRequest updateSalesUnitRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updateSalesUnitRequest);
		String salesCode = updateSalesUnitRequest.getSalesCode();
		String salesName = updateSalesUnitRequest.getSalesName();
		SalesUnit existingSalesUnit = this.findSalesUnitById(id);
		boolean exists = salesUnitRepository.existsBySalesCodeAndIdNotOrSalesNameAndIdNot(salesCode, id, salesName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingSalesUnit.getSalesCode().equals(salesCode)) {
				auditFields.add(new AuditFields(null, "Sales Code", existingSalesUnit.getSalesCode(), salesCode));
				existingSalesUnit.setSalesCode(salesCode);
			}
			if (!existingSalesUnit.getSalesName().equals(salesName)) {
				auditFields.add(new AuditFields(null, "Sales Name", existingSalesUnit.getSalesName(), salesName));
				existingSalesUnit.setSalesName(salesName);
			}
			if (!existingSalesUnit.getSalesStatus().equals(updateSalesUnitRequest.getSalesStatus())) {
				auditFields.add(new AuditFields(null, "Sales Status", existingSalesUnit.getSalesStatus(),
						updateSalesUnitRequest.getSalesStatus()));
				existingSalesUnit.setSalesStatus(updateSalesUnitRequest.getSalesStatus());
			}
			if (!existingSalesUnit.getDynamicFields().equals(updateSalesUnitRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateSalesUnitRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingSalesUnit.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingSalesUnit.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingSalesUnit.updateAuditHistory(auditFields); // Update the audit history
			SalesUnit updatedSalesUnit = salesUnitRepository.save(existingSalesUnit);
			return salesUnitMapper.mapToSalesUnitResponse(updatedSalesUnit);
		}
		throw new ResourceFoundException("Sales Unit already exists");
	}

	@Override
	public SalesUnitResponse updateSalesUnitStatus(@NonNull Long id) throws ResourceNotFoundException {
		SalesUnit existingSalesUnit = this.findSalesUnitById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingSalesUnit.getSalesStatus() != null) {
			auditFields.add(new AuditFields(null, "Sales Status", existingSalesUnit.getSalesStatus(),
					!existingSalesUnit.getSalesStatus()));
			existingSalesUnit.setSalesStatus(!existingSalesUnit.getSalesStatus());
		}
		existingSalesUnit.updateAuditHistory(auditFields);
		salesUnitRepository.save(existingSalesUnit);
		return salesUnitMapper.mapToSalesUnitResponse(existingSalesUnit);
	}

	@Override
	public List<SalesUnitResponse> updateBatchSalesUnitStatus(@NonNull List<Long> ids) {
		List<SalesUnit> salesUnits = salesUnitRepository.findAllById(ids);
		List<AuditFields> auditFields = new ArrayList<>();
		salesUnits.forEach(existingSalesUnit -> {
			if (existingSalesUnit.getSalesStatus() != null) {
				auditFields.add(new AuditFields(null, "Sales Status", existingSalesUnit.getSalesStatus(),
						!existingSalesUnit.getSalesStatus()));
				existingSalesUnit.setSalesStatus(!existingSalesUnit.getSalesStatus());
			}
			existingSalesUnit.updateAuditHistory(auditFields);
		});
		salesUnitRepository.saveAll(salesUnits);
		return salesUnits.stream().sorted(Comparator.comparing(SalesUnit::getId))
				.map(salesUnitMapper::mapToSalesUnitResponse).toList();
	}

	@Override
	public void deleteSalesUnitId(@NonNull Long id) throws ResourceNotFoundException {
		SalesUnit salesUnit = this.findSalesUnitById(id);
		if (salesUnit != null) {
			salesUnitRepository.delete(salesUnit);
		}
	}

	@Override
	public void deleteBatchSalesUnit(List<Long> ids) throws ResourceNotFoundException {
		List<SalesUnit> salesUnits = this.findAllById(ids);
		if (!salesUnits.isEmpty()) {
			salesUnitRepository.deleteAll(salesUnits);
		}
	}

	private void validateDynamicFields(SalesUnit salesUnit) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : salesUnit.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = SalesUnit.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private SalesUnit findSalesUnitById(@NonNull Long id) throws ResourceNotFoundException {
		return salesUnitRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("No Sales Unit found with this Id"));
	}

	private List<SalesUnit> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<SalesUnit> salesUnits = salesUnitRepository.findAllById(idSet);

		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Sales Unit with IDs " + missingIds + " not found");
		}

		return salesUnits;
	}

}
