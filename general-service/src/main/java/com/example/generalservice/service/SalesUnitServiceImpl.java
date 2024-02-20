package com.example.generalservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.generalservice.client.DynamicClient;
import com.example.generalservice.dto.request.SalesUnitRequest;
import com.example.generalservice.dto.response.SalesUnitResponse;
import com.example.generalservice.entity.AuditFields;
import com.example.generalservice.entity.SalesUnit;
import com.example.generalservice.exceptions.ResourceFoundException;
import com.example.generalservice.exceptions.ResourceNotFoundException;
import com.example.generalservice.repository.SalesUnitRepository;
import com.example.generalservice.service.interfaces.SalesUnitService;
import com.example.generalservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesUnitServiceImpl implements SalesUnitService {
	private final SalesUnitRepository salesUnitRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public SalesUnitResponse saveSalesUnit(SalesUnitRequest salesUnitRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(salesUnitRequest);
		String salesCode = salesUnitRequest.getSalesCode();
		String salesName = salesUnitRequest.getSalesName();
		boolean exists = salesUnitRepository.existsBySalesCodeOrSalesName(salesCode, salesName);
		if (!exists) {
			SalesUnit salesUnit = modelMapper.map(salesUnitRequest, SalesUnit.class);
			for (Map.Entry<String, Object> entryField : salesUnit.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = SalesUnit.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			SalesUnit savedSalesUnit = salesUnitRepository.save(salesUnit);
			return mapToSalesUnitResponse(savedSalesUnit);
		}
		throw new ResourceFoundException("Sales Unit already exists");
	}

	@Override
	@Cacheable("salesUnit")
	public SalesUnitResponse getSalesUnitById(Long id) throws ResourceNotFoundException {
		SalesUnit salesUnit = this.findSalesUnitById(id);
		return mapToSalesUnitResponse(salesUnit);
	}

	@Override
	@Cacheable("salesUnit")
	public List<SalesUnitResponse> getAllSalesUnit() {
		List<SalesUnit> salesUnitList = salesUnitRepository.findAll();
		return salesUnitList.stream().sorted(Comparator.comparing(SalesUnit::getId)).map(this::mapToSalesUnitResponse)
				.toList();
	}

	@Override
	@Cacheable("salesUnit")
	public List<SalesUnitResponse> findAllStatusTrue() {
		List<SalesUnit> salesUnits = salesUnitRepository.findAllBySalesStatusIsTrue();
		return salesUnits.stream().sorted(Comparator.comparing(SalesUnit::getId)).map(this::mapToSalesUnitResponse)
				.toList();
	}

	@Override
	public SalesUnitResponse updateSalesUnit(Long id, SalesUnitRequest updateSalesUnitRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
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
			return mapToSalesUnitResponse(updatedSalesUnit);
		}
		throw new ResourceFoundException("Sales Unit already exists");
	}

	@Override
	public SalesUnitResponse updateSalesUnitStatus(Long id) throws ResourceNotFoundException {
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
		return mapToSalesUnitResponse(existingSalesUnit);
	}

	@Override
	public List<SalesUnitResponse> updateBatchSalesUnitStatus(List<Long> ids) {
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
		return salesUnits.stream().sorted(Comparator.comparing(SalesUnit::getId)).map(this::mapToSalesUnitResponse)
				.toList();
	}

	@Override
	public void deleteSalesUnitId(Long id) throws ResourceNotFoundException {
		SalesUnit salesUnit = this.findSalesUnitById(id);
		salesUnitRepository.deleteById(salesUnit.getId());
	}

	@Override
	public void deleteBatchSalesUnit(List<Long> ids) throws ResourceNotFoundException {
		this.findAllById(ids);
		salesUnitRepository.deleteAllByIdInBatch(ids);
	}

	private SalesUnitResponse mapToSalesUnitResponse(SalesUnit salesUnit) {
		return modelMapper.map(salesUnit, SalesUnitResponse.class);
	}

	private SalesUnit findSalesUnitById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<SalesUnit> salesUnit = salesUnitRepository.findById(id);
		if (salesUnit.isEmpty()) {
			throw new ResourceNotFoundException("No Sales Unit found with this Id");
		}
		return salesUnit.get();
	}

	private List<SalesUnit> findAllById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<SalesUnit> salesUnits = salesUnitRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> salesUnits.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Sales Unit with IDs " + missingIds + " not found");
		}
		return salesUnits;
	}

}
