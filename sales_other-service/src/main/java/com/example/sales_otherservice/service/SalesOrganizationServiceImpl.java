package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.SalesOrganizationRequest;
import com.example.sales_otherservice.dto.response.SalesOrganizationResponse;
import com.example.sales_otherservice.entity.SalesOrganization;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.SalesOrganizationRepository;
import com.example.sales_otherservice.service.interfaces.SalesOrganizationService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesOrganizationServiceImpl implements SalesOrganizationService {
	private final SalesOrganizationRepository salesOrganizationRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public SalesOrganizationResponse saveSo(SalesOrganizationRequest salesOrganizationRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String soCode = salesOrganizationRequest.getSoCode();
		String soName = salesOrganizationRequest.getSoName();
		boolean exists = salesOrganizationRepository.existsBySoCodeOrSoName(soCode, soName);
		if (!exists) {

			SalesOrganization salesOrganization = modelMapper.map(salesOrganizationRequest, SalesOrganization.class);
			for (Map.Entry<String, Object> entryField : salesOrganization.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = SalesOrganization.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			SalesOrganization savedSalesOrganization = salesOrganizationRepository.save(salesOrganization);
			return mapToSalesOrganizationResponse(savedSalesOrganization);
		}
		throw new ResourceFoundException("Sales Organization Already exists");
	}

	@Override
	public SalesOrganizationResponse getSoById(Long id) throws ResourceNotFoundException {
		SalesOrganization salesOrganization = this.findSoById(id);
		return mapToSalesOrganizationResponse(salesOrganization);
	}

	@Override
	public List<SalesOrganizationResponse> getAllSo() {
		List<SalesOrganization> salesOrganizations = salesOrganizationRepository.findAll();
		return salesOrganizations.stream().sorted(Comparator.comparing(SalesOrganization::getId))
				.map(this::mapToSalesOrganizationResponse).toList();
	}

	@Override
	public List<SalesOrganizationResponse> findAllStatusTrue() {
		List<SalesOrganization> salesOrganizations = salesOrganizationRepository.findAllBySoStatusIsTrue();
		return salesOrganizations.stream().sorted(Comparator.comparing(SalesOrganization::getId))
				.map(this::mapToSalesOrganizationResponse).toList();
	}

	@Override
	public SalesOrganizationResponse updateSo(Long id, SalesOrganizationRequest updateSalesOrganizationRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		String soCode = updateSalesOrganizationRequest.getSoCode();
		String soName = updateSalesOrganizationRequest.getSoName();
		SalesOrganization existingSalesOrganization = this.findSoById(id);
		boolean exists = salesOrganizationRepository.existsBySoCodeAndIdNotOrSoNameAndIdNot(soCode, id, soName, id);
		if (!exists) {
			modelMapper.map(updateSalesOrganizationRequest, existingSalesOrganization);
			for (Map.Entry<String, Object> entryField : existingSalesOrganization.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = SalesOrganization.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			SalesOrganization updatedSalesOrganization = salesOrganizationRepository.save(existingSalesOrganization);
			return mapToSalesOrganizationResponse(updatedSalesOrganization);
		}
		throw new ResourceFoundException("Sales Organization Already exists");
	}

	@Override
	public SalesOrganizationResponse updateSoStatus(Long id) throws ResourceNotFoundException {
		SalesOrganization salesOrganization = this.findSoById(id);
		salesOrganization.setSoStatus(!salesOrganization.getSoStatus());
		salesOrganizationRepository.save(salesOrganization);
		return mapToSalesOrganizationResponse(salesOrganization);
	}

	@Override
	public List<SalesOrganizationResponse> updateBatchSoStatus(List<Long> ids) throws ResourceNotFoundException {
		List<SalesOrganization> organizations = this.findAllSoById(ids);
		organizations.forEach(organization -> organization.setSoStatus(!organization.getSoStatus()));
		salesOrganizationRepository.saveAll(organizations);
		return organizations.stream().map(this::mapToSalesOrganizationResponse).toList();
	}

	@Override
	public void deleteSoById(Long id) throws ResourceNotFoundException {
		SalesOrganization salesOrganization = this.findSoById(id);
		salesOrganizationRepository.deleteById(salesOrganization.getId());
	}

	@Override
	public void deleteBatchSo(List<Long> ids) throws ResourceNotFoundException {
		this.findAllSoById(ids);
		salesOrganizationRepository.deleteAllByIdInBatch(ids);
	}

	private SalesOrganizationResponse mapToSalesOrganizationResponse(SalesOrganization salesOrganization) {
		return modelMapper.map(salesOrganization, SalesOrganizationResponse.class);
	}

	private SalesOrganization findSoById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<SalesOrganization> salesOrganization = salesOrganizationRepository.findById(id);
		if (salesOrganization.isEmpty()) {
			throw new ResourceNotFoundException("Sales Organization Key not found with this Id");
		}
		return salesOrganization.get();
	}

	private List<SalesOrganization> findAllSoById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<SalesOrganization> salesOrganizations = salesOrganizationRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> salesOrganizations.stream().noneMatch(entity -> entity.getId().equals(id))).toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Sales Organization with IDs " + missingIds + " not found");
		}
		return salesOrganizations;
	}

}
