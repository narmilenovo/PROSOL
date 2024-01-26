package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.TaxClassificationTypeRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationTypeResponse;
import com.example.sales_otherservice.entity.TaxClassificationType;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.TaxClassificationTypeRepository;
import com.example.sales_otherservice.service.interfaces.TaxClassificationTypeService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaxClassificationTypeServiceImpl implements TaxClassificationTypeService {
	private final TaxClassificationTypeRepository taxClassificationTypeRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public TaxClassificationTypeResponse saveTct(TaxClassificationTypeRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String tctCode = taxClassificationClassRequest.getTctCode();
		String tctName = taxClassificationClassRequest.getTctName();
		boolean exists = taxClassificationTypeRepository.existsByTctCodeOrTctName(tctCode, tctName);
		if (!exists) {

			TaxClassificationType classificationType = modelMapper.map(taxClassificationClassRequest,
					TaxClassificationType.class);
			for (Map.Entry<String, Object> entryField : classificationType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = TaxClassificationType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			TaxClassificationType savedClassificationType = taxClassificationTypeRepository.save(classificationType);
			return mapToTaxClassificationClassResponse(savedClassificationType);
		}
		throw new ResourceFoundException("Tax classification Type Already exists");
	}

	@Override
	public TaxClassificationTypeResponse getTctById(Long id) throws ResourceNotFoundException {
		TaxClassificationType classificationType = this.findTctById(id);
		return mapToTaxClassificationClassResponse(classificationType);
	}

	@Override
	public List<TaxClassificationTypeResponse> getAllTct() {
		List<TaxClassificationType> classificationTypes = taxClassificationTypeRepository.findAll();
		return classificationTypes.stream().sorted(Comparator.comparing(TaxClassificationType::getId))
				.map(this::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public List<TaxClassificationTypeResponse> findAllStatusTrue() {
		List<TaxClassificationType> classificationTypes = taxClassificationTypeRepository.findAllByTctStatusIsTrue();
		return classificationTypes.stream().sorted(Comparator.comparing(TaxClassificationType::getId))
				.map(this::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public TaxClassificationTypeResponse updateTct(Long id,
			TaxClassificationTypeRequest updateTaxClassificationTypeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		String tctCode = updateTaxClassificationTypeRequest.getTctCode();
		String tctName = updateTaxClassificationTypeRequest.getTctName();
		TaxClassificationType existingClassificationType = this.findTctById(id);
		boolean exists = taxClassificationTypeRepository.existsByTctCodeAndIdNotOrTctNameAndIdNot(tctCode, id, tctName,
				id);
		if (!exists) {
			modelMapper.map(updateTaxClassificationTypeRequest, existingClassificationType);
			for (Map.Entry<String, Object> entryField : existingClassificationType.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = TaxClassificationType.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			TaxClassificationType updateClassificationType = taxClassificationTypeRepository
					.save(existingClassificationType);
			return mapToTaxClassificationClassResponse(updateClassificationType);
		}
		throw new ResourceFoundException("Tax classification Type Already exists");
	}

	@Override
	public TaxClassificationTypeResponse updateTctStatus(Long id) throws ResourceNotFoundException {
		TaxClassificationType classificationType = this.findTctById(id);
		classificationType.setTctStatus(!classificationType.getTctStatus());
		taxClassificationTypeRepository.save(classificationType);
		return mapToTaxClassificationClassResponse(classificationType);
	}

	@Override
	public List<TaxClassificationTypeResponse> updateBatchTctStatus(List<Long> ids) throws ResourceNotFoundException {
		List<TaxClassificationType> types = this.findAllTctById(ids);
		types.forEach(type -> type.setTctStatus(!type.getTctStatus()));
		taxClassificationTypeRepository.saveAll(types);
		return types.stream().map(this::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public void deleteTctById(Long id) throws ResourceNotFoundException {
		TaxClassificationType classificationType = this.findTctById(id);
		taxClassificationTypeRepository.deleteById(classificationType.getId());
	}

	@Override
	public void deleteBatchTct(List<Long> ids) throws ResourceNotFoundException {
		this.findAllTctById(ids);
		taxClassificationTypeRepository.deleteAllByIdInBatch(ids);
	}

	private TaxClassificationTypeResponse mapToTaxClassificationClassResponse(
			TaxClassificationType taxClassificationType) {
		return modelMapper.map(taxClassificationType, TaxClassificationTypeResponse.class);
	}

	private TaxClassificationType findTctById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<TaxClassificationType> classificationType = taxClassificationTypeRepository.findById(id);
		if (classificationType.isEmpty()) {
			throw new ResourceNotFoundException("Tax classification Type not found with this Id");
		}
		return classificationType.get();
	}

	private List<TaxClassificationType> findAllTctById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<TaxClassificationType> types = taxClassificationTypeRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream().filter(id -> types.stream().noneMatch(entity -> entity.getId().equals(id)))
				.toList();

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Tax classification Type with IDs " + missingIds + " not found.");
		}
		return types;
	}

}
