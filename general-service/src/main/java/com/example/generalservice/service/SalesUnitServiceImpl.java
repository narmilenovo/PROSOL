package com.example.generalservice.service;

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
		String salesCode = updateSalesUnitRequest.getSalesCode();
		String salesName = updateSalesUnitRequest.getSalesName();
		SalesUnit existingSalesUnit = this.findSalesUnitById(id);
		boolean exists = salesUnitRepository.existsBySalesCodeAndIdNotOrSalesNameAndIdNot(salesCode, id, salesName, id);
		if (!exists) {
			modelMapper.map(updateSalesUnitRequest, existingSalesUnit);
			for (Map.Entry<String, Object> entryField : existingSalesUnit.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = SalesUnit.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			SalesUnit updatedSalesUnit = salesUnitRepository.save(existingSalesUnit);
			return mapToSalesUnitResponse(updatedSalesUnit);
		}
		throw new ResourceFoundException("Sales Unit already exists");
	}

	@Override
	public SalesUnitResponse updateSalesUnitStatus(Long id) throws ResourceNotFoundException {
		SalesUnit existingSalesUnit = this.findSalesUnitById(id);
		existingSalesUnit.setSalesStatus(!existingSalesUnit.getSalesStatus());
		salesUnitRepository.save(existingSalesUnit);
		return mapToSalesUnitResponse(existingSalesUnit);
	}

	@Override
	public List<SalesUnitResponse> updateBatchSalesUnitStatus(List<Long> ids) {
		List<SalesUnit> salesUnits = salesUnitRepository.findAllById(ids);
		for (SalesUnit salesUnit : salesUnits) {
			salesUnit.setSalesStatus(!salesUnit.getSalesStatus());
		}
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
