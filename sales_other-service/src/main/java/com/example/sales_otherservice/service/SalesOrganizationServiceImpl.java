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
import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.SalesOrganization;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.SalesOrganizationMapper;
import com.example.sales_otherservice.repository.SalesOrganizationRepository;
import com.example.sales_otherservice.service.interfaces.SalesOrganizationService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesOrganizationServiceImpl implements SalesOrganizationService {
	private final SalesOrganizationRepository salesOrganizationRepository;
	private final SalesOrganizationMapper organizationMapper;
	private final DynamicClient dynamicClient;

	@Override
	public SalesOrganizationResponse saveSo(SalesOrganizationRequest salesOrganizationRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(salesOrganizationRequest);
		String soCode = salesOrganizationRequest.getSoCode();
		String soName = salesOrganizationRequest.getSoName();
		if (salesOrganizationRepository.existsBySoCodeOrSoName(soCode, soName)) {
			throw new ResourceFoundException("Sales Organization Already exists");
		}

		SalesOrganization salesOrganization = organizationMapper.mapToSalesOrganization(salesOrganizationRequest);

		validateDynamicFields(salesOrganization);

		SalesOrganization savedSalesOrganization = salesOrganizationRepository.save(salesOrganization);
		return organizationMapper.mapToSalesOrganizationResponse(savedSalesOrganization);
	}

	@Override
	public SalesOrganizationResponse getSoById(@NonNull Long id) throws ResourceNotFoundException {
		SalesOrganization salesOrganization = this.findSoById(id);
		return organizationMapper.mapToSalesOrganizationResponse(salesOrganization);
	}

	@Override
	public List<SalesOrganizationResponse> getAllSo() {
		List<SalesOrganization> salesOrganizations = salesOrganizationRepository.findAll();
		return salesOrganizations.stream().sorted(Comparator.comparing(SalesOrganization::getId))
				.map(organizationMapper::mapToSalesOrganizationResponse).toList();
	}

	@Override
	public List<SalesOrganizationResponse> findAllStatusTrue() {
		List<SalesOrganization> salesOrganizations = salesOrganizationRepository.findAllBySoStatusIsTrue();
		return salesOrganizations.stream().sorted(Comparator.comparing(SalesOrganization::getId))
				.map(organizationMapper::mapToSalesOrganizationResponse).toList();
	}

	@Override
	public SalesOrganizationResponse updateSo(@NonNull Long id, SalesOrganizationRequest updateSalesOrganizationRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updateSalesOrganizationRequest);
		String soCode = updateSalesOrganizationRequest.getSoCode();
		String soName = updateSalesOrganizationRequest.getSoName();
		SalesOrganization existingSalesOrganization = this.findSoById(id);
		boolean exists = salesOrganizationRepository.existsBySoCodeAndIdNotOrSoNameAndIdNot(soCode, id, soName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingSalesOrganization.getSoCode().equals(soCode)) {
				auditFields.add(new AuditFields(null, "So Code", existingSalesOrganization.getSoCode(), soCode));
				existingSalesOrganization.setSoCode(soCode);
			}
			if (!existingSalesOrganization.getSoName().equals(soName)) {
				auditFields.add(new AuditFields(null, "So Name", existingSalesOrganization.getSoName(), soName));
				existingSalesOrganization.setSoName(soName);
			}
			if (!existingSalesOrganization.getSoStatus().equals(updateSalesOrganizationRequest.getSoStatus())) {
				auditFields.add(new AuditFields(null, "So Status", existingSalesOrganization.getSoStatus(),
						updateSalesOrganizationRequest.getSoStatus()));
				existingSalesOrganization.setSoStatus(updateSalesOrganizationRequest.getSoStatus());
			}
			if (!existingSalesOrganization.getDynamicFields()
					.equals(updateSalesOrganizationRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateSalesOrganizationRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingSalesOrganization.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingSalesOrganization.getDynamicFields().put(fieldName, newValue);
					}
				}
			}
			existingSalesOrganization.updateAuditHistory(auditFields);
			SalesOrganization updatedSalesOrganization = salesOrganizationRepository.save(existingSalesOrganization);
			return organizationMapper.mapToSalesOrganizationResponse(updatedSalesOrganization);
		}
		throw new ResourceFoundException("Sales Organization Already exists");
	}

	@Override
	public SalesOrganizationResponse updateSoStatus(@NonNull Long id) throws ResourceNotFoundException {
		SalesOrganization existingSalesOrganization = this.findSoById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingSalesOrganization.getSoStatus() != null) {
			auditFields.add(new AuditFields(null, "So Status", existingSalesOrganization.getSoStatus(),
					!existingSalesOrganization.getSoStatus()));
			existingSalesOrganization.setSoStatus(!existingSalesOrganization.getSoStatus());
		}
		existingSalesOrganization.updateAuditHistory(auditFields);
		salesOrganizationRepository.save(existingSalesOrganization);
		return organizationMapper.mapToSalesOrganizationResponse(existingSalesOrganization);
	}

	@Override
	public List<SalesOrganizationResponse> updateBatchSoStatus(@NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<SalesOrganization> organizations = this.findAllSoById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		organizations.forEach(existingSalesOrganization -> {
			if (existingSalesOrganization.getSoStatus() != null) {
				auditFields.add(new AuditFields(null, "So Status", existingSalesOrganization.getSoStatus(),
						!existingSalesOrganization.getSoStatus()));
				existingSalesOrganization.setSoStatus(!existingSalesOrganization.getSoStatus());
			}
			existingSalesOrganization.updateAuditHistory(auditFields);

		});
		salesOrganizationRepository.saveAll(organizations);
		return organizations.stream().map(organizationMapper::mapToSalesOrganizationResponse).toList();
	}

	@Override
	public void deleteSoById(@NonNull Long id) throws ResourceNotFoundException {
		SalesOrganization salesOrganization = this.findSoById(id);
		if (salesOrganization != null) {
			salesOrganizationRepository.delete(salesOrganization);
		}
	}

	@Override
	public void deleteBatchSo(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<SalesOrganization> salesOrganizations = this.findAllSoById(ids);
		if (!salesOrganizations.isEmpty()) {
			salesOrganizationRepository.deleteAll(salesOrganizations);
		}
	}

	private void validateDynamicFields(SalesOrganization salesOrganization) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : salesOrganization.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = SalesOrganization.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private SalesOrganization findSoById(@NonNull Long id) throws ResourceNotFoundException {
		return salesOrganizationRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Sales Organization Key not found with this Id"));
	}

	private List<SalesOrganization> findAllSoById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<SalesOrganization> salesOrganizations = salesOrganizationRepository.findAllById(ids);

		Map<Long, SalesOrganization> salesOrganizationMap = salesOrganizations.stream()
				.collect(Collectors.toMap(SalesOrganization::getId, Function.identity()));

		List<Long> missingIds = ids.stream().filter(id -> !salesOrganizationMap.containsKey(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Sales Organization with IDs " + missingIds + " not found");
		}

		return salesOrganizations;
	}

}
