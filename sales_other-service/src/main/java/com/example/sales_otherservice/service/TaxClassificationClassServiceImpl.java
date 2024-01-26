package com.example.sales_otherservice.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.example.sales_otherservice.clients.Dynamic.DynamicClient;
import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.entity.TaxClassificationClass;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.repository.TaxClassificationClassRepository;
import com.example.sales_otherservice.service.interfaces.TaxClassificationClassService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaxClassificationClassServiceImpl implements TaxClassificationClassService {
	private final TaxClassificationClassRepository taxClassificationClassRepository;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	@Override
	public TaxClassificationClassResponse saveTcc(TaxClassificationClassRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		String tccCode = taxClassificationClassRequest.getTccCode();
		String tccName = taxClassificationClassRequest.getTccName();
		boolean exists = taxClassificationClassRepository.existsByTccCodeOrTccName(tccCode, tccName);
		if (!exists) {

			TaxClassificationClass classificationClass = modelMapper.map(taxClassificationClassRequest,
					TaxClassificationClass.class);
			for (Map.Entry<String, Object> entryField : classificationClass.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = TaxClassificationClass.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			TaxClassificationClass savedClassificationClass = taxClassificationClassRepository
					.save(classificationClass);
			return mapToTaxClassificationClassResponse(savedClassificationClass);
		}
		throw new ResourceFoundException("Tax classification Already exists");
	}

	@Override
	public TaxClassificationClassResponse getTccById(Long id) throws ResourceNotFoundException {
		TaxClassificationClass classificationClass = this.findTccById(id);
		return mapToTaxClassificationClassResponse(classificationClass);
	}

	@Override
	public List<TaxClassificationClassResponse> getAllTcc() {
		List<TaxClassificationClass> taxClassificationClasses = taxClassificationClassRepository.findAll();
		return taxClassificationClasses.stream().sorted(Comparator.comparing(TaxClassificationClass::getId))
				.map(this::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public List<TaxClassificationClassResponse> findAllStatusTrue() {
		List<TaxClassificationClass> taxClassificationClasses = taxClassificationClassRepository
				.findAllByTccStatusIsTrue();
		return taxClassificationClasses.stream().sorted(Comparator.comparing(TaxClassificationClass::getId))
				.map(this::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public TaxClassificationClassResponse updateTcc(Long id,
			TaxClassificationClassRequest updateTaxClassificationClassRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		String tccCode = updateTaxClassificationClassRequest.getTccCode();
		String tccName = updateTaxClassificationClassRequest.getTccName();
		TaxClassificationClass existingClassificationClass = this.findTccById(id);
		boolean exists = taxClassificationClassRepository.existsByTccCodeAndIdNotOrTccNameAndIdNot(tccCode, id, tccName,
				id);
		if (!exists) {
			modelMapper.map(updateTaxClassificationClassRequest, existingClassificationClass);
			for (Map.Entry<String, Object> entryField : existingClassificationClass.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = TaxClassificationClass.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			TaxClassificationClass updatedClassificationClass = taxClassificationClassRepository
					.save(existingClassificationClass);
			return mapToTaxClassificationClassResponse(updatedClassificationClass);
		}
		throw new ResourceFoundException("Tax classification Already exists");
	}

	@Override
	public TaxClassificationClassResponse updateTccStatus(Long id) throws ResourceNotFoundException {
		TaxClassificationClass classificationClass = this.findTccById(id);
		classificationClass.setTccStatus(!classificationClass.getTccStatus());
		taxClassificationClassRepository.save(classificationClass);
		return mapToTaxClassificationClassResponse(classificationClass);
	}

	@Override
	public List<TaxClassificationClassResponse> updateBatchTccStatus(List<Long> ids) throws ResourceNotFoundException {
		List<TaxClassificationClass> classes = this.findAllTccById(ids);
		classes.forEach(classificationClass -> classificationClass.setTccStatus(!classificationClass.getTccStatus()));
		taxClassificationClassRepository.saveAll(classes);
		return classes.stream().map(this::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public void deleteTccById(Long id) throws ResourceNotFoundException {
		TaxClassificationClass classificationClass = this.findTccById(id);
		taxClassificationClassRepository.deleteById(classificationClass.getId());

	}

	@Override
	public void deleteBatchTcc(List<Long> ids) throws ResourceNotFoundException {
		this.findAllTccById(ids);
		taxClassificationClassRepository.deleteAllByIdInBatch(ids);
	}

	private TaxClassificationClassResponse mapToTaxClassificationClassResponse(
			TaxClassificationClass taxClassificationClass) {
		return modelMapper.map(taxClassificationClass, TaxClassificationClassResponse.class);
	}

	private TaxClassificationClass findTccById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<TaxClassificationClass> taxClassificationClass = taxClassificationClassRepository.findById(id);
		if (taxClassificationClass.isEmpty()) {
			throw new ResourceNotFoundException("Tax classification Class not found with this Id");
		}
		return taxClassificationClass.get();
	}

	private List<TaxClassificationClass> findAllTccById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<TaxClassificationClass> taxClassificationClasses = taxClassificationClassRepository.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> taxClassificationClasses.stream().noneMatch(entity -> entity.getId().equals(id)))
				.toList();
		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Tax classification Class with IDs " + missingIds + " not found");
		}
		return taxClassificationClasses;
	}

}
