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
import com.example.sales_otherservice.dto.request.TaxClassificationClassRequest;
import com.example.sales_otherservice.dto.response.TaxClassificationClassResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.TaxClassificationClass;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.TaxClassificationClassMapper;
import com.example.sales_otherservice.repository.TaxClassificationClassRepository;
import com.example.sales_otherservice.service.interfaces.TaxClassificationClassService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaxClassificationClassServiceImpl implements TaxClassificationClassService {
	private final TaxClassificationClassRepository taxClassificationClassRepository;
	private final TaxClassificationClassMapper classificationClassMapper;
	private final DynamicClient dynamicClient;

	@Override
	public TaxClassificationClassResponse saveTcc(TaxClassificationClassRequest taxClassificationClassRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(taxClassificationClassRequest);
		String tccCode = taxClassificationClassRequest.getTccCode();
		String tccName = taxClassificationClassRequest.getTccName();
		if (taxClassificationClassRepository.existsByTccCodeOrTccName(tccCode, tccName)) {
			throw new ResourceFoundException("Tax classification Already exists");
		}

		TaxClassificationClass classificationClass = classificationClassMapper
				.mapToTaxClassificationClass(taxClassificationClassRequest);

		validateDynamicFields(classificationClass);

		TaxClassificationClass savedClassificationClass = taxClassificationClassRepository.save(classificationClass);
		return classificationClassMapper.mapToTaxClassificationClassResponse(savedClassificationClass);
	}

	@Override
	public TaxClassificationClassResponse getTccById(@NonNull Long id) throws ResourceNotFoundException {
		TaxClassificationClass classificationClass = this.findTccById(id);
		return classificationClassMapper.mapToTaxClassificationClassResponse(classificationClass);
	}

	@Override
	public List<TaxClassificationClassResponse> getAllTcc() {
		return taxClassificationClassRepository.findAll().stream()
				.sorted(Comparator.comparing(TaxClassificationClass::getId))
				.map(classificationClassMapper::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public List<TaxClassificationClassResponse> findAllStatusTrue() {
		return taxClassificationClassRepository.findAllByTccStatusIsTrue().stream()
				.sorted(Comparator.comparing(TaxClassificationClass::getId))
				.map(classificationClassMapper::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public TaxClassificationClassResponse updateTcc(@NonNull Long id,
			TaxClassificationClassRequest updateTaxClassificationClassRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.inputTitleCase(updateTaxClassificationClassRequest);
		String tccCode = updateTaxClassificationClassRequest.getTccCode();
		String tccName = updateTaxClassificationClassRequest.getTccName();
		TaxClassificationClass existingClassificationClass = this.findTccById(id);
		boolean exists = taxClassificationClassRepository.existsByTccCodeAndIdNotOrTccNameAndIdNot(tccCode, id, tccName,
				id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingClassificationClass.getTccCode().equals(tccCode)) {
				auditFields.add(new AuditFields(null, "Tcc Code", existingClassificationClass.getTccCode(), tccCode));
				existingClassificationClass.setTccCode(tccCode);
			}
			if (!existingClassificationClass.getTccName().equals(tccName)) {
				auditFields.add(new AuditFields(null, "Tcc Name", existingClassificationClass.getTccName(), tccName));
				existingClassificationClass.setTccName(tccName);
			}
			if (!existingClassificationClass.getTccStatus()
					.equals(updateTaxClassificationClassRequest.getTccStatus())) {
				auditFields.add(new AuditFields(null, "Tcc Status", existingClassificationClass.getTccStatus(),
						updateTaxClassificationClassRequest.getTccStatus()));
				existingClassificationClass.setTccStatus(updateTaxClassificationClassRequest.getTccStatus());
			}
			if (!existingClassificationClass.getDynamicFields()
					.equals(updateTaxClassificationClassRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateTaxClassificationClassRequest.getDynamicFields()
						.entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingClassificationClass.getDynamicFields().get(fieldName);
					if (!newValue.equals(oldValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingClassificationClass.getDynamicFields().put(fieldName, newValue);
					}
				}
			}
			existingClassificationClass.updateAuditHistory(auditFields);
			TaxClassificationClass updatedClassificationClass = taxClassificationClassRepository
					.save(existingClassificationClass);
			return classificationClassMapper.mapToTaxClassificationClassResponse(updatedClassificationClass);
		}
		throw new ResourceFoundException("Tax classification Already exists");
	}

	@Override
	public TaxClassificationClassResponse updateTccStatus(@NonNull Long id) throws ResourceNotFoundException {
		TaxClassificationClass existingClassificationClass = this.findTccById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingClassificationClass.getTccStatus() != null) {
			auditFields.add(new AuditFields(null, "Tcc Status", existingClassificationClass.getTccStatus(),
					!existingClassificationClass.getTccStatus()));
			existingClassificationClass.setTccStatus(!existingClassificationClass.getTccStatus());
		}
		existingClassificationClass.updateAuditHistory(auditFields);
		taxClassificationClassRepository.save(existingClassificationClass);
		return classificationClassMapper.mapToTaxClassificationClassResponse(existingClassificationClass);
	}

	@Override
	public List<TaxClassificationClassResponse> updateBatchTccStatus(@NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<TaxClassificationClass> classes = this.findAllTccById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		classes.forEach(existingClassificationClass -> {
			if (existingClassificationClass.getTccStatus() != null) {
				auditFields.add(new AuditFields(null, "Tcc Status", existingClassificationClass.getTccStatus(),
						!existingClassificationClass.getTccStatus()));
				existingClassificationClass.setTccStatus(!existingClassificationClass.getTccStatus());
			}
			existingClassificationClass.updateAuditHistory(auditFields);

		});
		taxClassificationClassRepository.saveAll(classes);
		return classes.stream().map(classificationClassMapper::mapToTaxClassificationClassResponse).toList();
	}

	@Override
	public void deleteTccById(@NonNull Long id) throws ResourceNotFoundException {
		TaxClassificationClass classificationClass = this.findTccById(id);
		if (classificationClass != null) {
			taxClassificationClassRepository.delete(classificationClass);
		}

	}

	@Override
	public void deleteBatchTcc(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<TaxClassificationClass> classificationClasses = this.findAllTccById(ids);
		if (!classificationClasses.isEmpty()) {
			taxClassificationClassRepository.deleteAll(classificationClasses);
		}
	}

	private void validateDynamicFields(TaxClassificationClass classificationClass) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : classificationClass.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = TaxClassificationClass.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private TaxClassificationClass findTccById(@NonNull Long id) throws ResourceNotFoundException {
		return taxClassificationClassRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Tax classification Class not found with this Id"));
	}

	private List<TaxClassificationClass> findAllTccById(@NonNull List<Long> ids) throws ResourceNotFoundException {

		List<TaxClassificationClass> taxClassificationClasses = taxClassificationClassRepository.findAllById(ids);

		Map<Long, TaxClassificationClass> taxClassificationClassMap = taxClassificationClasses.stream()
				.collect(Collectors.toMap(TaxClassificationClass::getId, Function.identity()));

		List<Long> missingIds = ids.stream().filter(id -> !taxClassificationClassMap.containsKey(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Tax classification Class with IDs " + missingIds + " not found");
		}

		return taxClassificationClasses;
	}

}
