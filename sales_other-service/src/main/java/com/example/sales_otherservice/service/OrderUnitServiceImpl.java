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
import com.example.sales_otherservice.dto.request.OrderUnitRequest;
import com.example.sales_otherservice.dto.response.OrderUnitResponse;
import com.example.sales_otherservice.entity.AuditFields;
import com.example.sales_otherservice.entity.OrderUnit;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;
import com.example.sales_otherservice.mapping.OrderUnitMapper;
import com.example.sales_otherservice.repository.OrderUnitRepository;
import com.example.sales_otherservice.service.interfaces.OrderUnitService;
import com.example.sales_otherservice.utils.Helpers;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderUnitServiceImpl implements OrderUnitService {
	private final OrderUnitRepository orderUnitRepository;
	private final OrderUnitMapper orderUnitMapper;
	private final DynamicClient dynamicClient;

	@Override
	public OrderUnitResponse saveOu(OrderUnitRequest orderUnitRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		Helpers.inputTitleCase(orderUnitRequest);
		String ouCode = orderUnitRequest.getOuCode();
		String ouName = orderUnitRequest.getOuName();
		if (orderUnitRepository.existsByOuCodeOrOuName(ouCode, ouName)) {
			throw new ResourceFoundException("Order Unit Already exists");
		}

		OrderUnit orderUnit = orderUnitMapper.mapToOrderUnit(orderUnitRequest);
		validateDynamicFields(orderUnit);
		OrderUnit savedUnit = orderUnitRepository.save(orderUnit);
		return orderUnitMapper.mapToOrderUnitResponse(savedUnit);
	}

	@Override
	public List<OrderUnitResponse> getAllOu() {
		List<OrderUnit> orderUnits = orderUnitRepository.findAll();
		return orderUnits.stream().sorted(Comparator.comparing(OrderUnit::getId))
				.map(orderUnitMapper::mapToOrderUnitResponse).toList();

	}

	@Override
	public OrderUnitResponse getOuById(@NonNull Long id) throws ResourceNotFoundException {
		OrderUnit orderUnit = this.findOuById(id);
		return orderUnitMapper.mapToOrderUnitResponse(orderUnit);
	}

	@Override
	public List<OrderUnitResponse> findAllStatusTrue() {
		List<OrderUnit> orderUnits = orderUnitRepository.findAllByOuStatusIsTrue();
		return orderUnits.stream().sorted(Comparator.comparing(OrderUnit::getId))
				.map(orderUnitMapper::mapToOrderUnitResponse).toList();
	}

	@Override
	public OrderUnitResponse updateOu(@NonNull Long id, OrderUnitRequest updateOrderUnitRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(updateOrderUnitRequest);
		String ouCode = updateOrderUnitRequest.getOuCode();
		String ouName = updateOrderUnitRequest.getOuName();
		OrderUnit existingOrderUnit = this.findOuById(id);
		boolean exists = orderUnitRepository.existsByOuCodeAndIdNotOrOuNameAndIdNot(ouCode, id, ouName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			if (!existingOrderUnit.getOuCode().equals(ouCode)) {
				auditFields.add(new AuditFields(null, "Ou Code", existingOrderUnit.getOuCode(), ouCode));
				existingOrderUnit.setOuCode(ouCode);
			}
			if (!existingOrderUnit.getOuName().equals(ouName)) {
				auditFields.add(new AuditFields(null, "Ou Name", existingOrderUnit.getOuName(), ouName));
				existingOrderUnit.setOuName(ouName);
			}
			if (!existingOrderUnit.getOuStatus().equals(updateOrderUnitRequest.getOuStatus())) {
				auditFields.add(new AuditFields(null, "Ou Status", existingOrderUnit.getOuStatus(),
						updateOrderUnitRequest.getOuStatus()));
				existingOrderUnit.setOuStatus(updateOrderUnitRequest.getOuStatus());
			}
			if (!existingOrderUnit.getDynamicFields().equals(updateOrderUnitRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : updateOrderUnitRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingOrderUnit.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingOrderUnit.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingOrderUnit.updateAuditHistory(auditFields);
			OrderUnit updatedOrderUnit = orderUnitRepository.save(existingOrderUnit);
			return orderUnitMapper.mapToOrderUnitResponse(updatedOrderUnit);
		}
		throw new ResourceFoundException("Order Unit Already exists");
	}

	@Override
	public OrderUnitResponse updateOuStatus(@NonNull Long id) throws ResourceNotFoundException {
		OrderUnit existingOrderUnit = this.findOuById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingOrderUnit.getOuStatus() != null) {
			auditFields.add(new AuditFields(null, "Ou Status", existingOrderUnit.getOuStatus(),
					!existingOrderUnit.getOuStatus()));
			existingOrderUnit.setOuStatus(!existingOrderUnit.getOuStatus());
		}
		existingOrderUnit.updateAuditHistory(auditFields);
		OrderUnit updatedOrderUnit = orderUnitRepository.save(existingOrderUnit);
		return orderUnitMapper.mapToOrderUnitResponse(updatedOrderUnit);
	}

	@Override
	public List<OrderUnitResponse> updateBatchOuStatus(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<OrderUnit> orderUnits = this.findAllOuById(ids);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		orderUnits.forEach(existingOrderUnit -> {
			if (existingOrderUnit.getOuStatus() != null) {
				auditFields.add(new AuditFields(null, "Ou Status", existingOrderUnit.getOuStatus(),
						!existingOrderUnit.getOuStatus()));
				existingOrderUnit.setOuStatus(!existingOrderUnit.getOuStatus());
			}
			existingOrderUnit.updateAuditHistory(auditFields);

		});
		orderUnitRepository.saveAll(orderUnits);
		return orderUnits.stream().map(orderUnitMapper::mapToOrderUnitResponse).toList();

	}

	@Override
	public void deleteOuById(@NonNull Long id) throws ResourceNotFoundException {
		OrderUnit orderUnit = this.findOuById(id);
		if (orderUnit != null) {
			orderUnitRepository.delete(orderUnit);
		}
	}

	@Override
	public void deleteBatchOu(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<OrderUnit> orderUnits = this.findAllOuById(ids);
		if (!orderUnits.isEmpty()) {
			orderUnitRepository.deleteAll(orderUnits);
		}
	}

	private void validateDynamicFields(OrderUnit orderUnit) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : orderUnit.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = OrderUnit.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private OrderUnit findOuById(@NonNull Long id) throws ResourceNotFoundException {
		return orderUnitRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order Unit not found with this Id"));
	}

	private List<OrderUnit> findAllOuById(@NonNull List<Long> ids) throws ResourceNotFoundException {
		List<OrderUnit> orderUnits = orderUnitRepository.findAllById(ids);

		Map<Long, OrderUnit> orderUnitMap = orderUnits.stream()
				.collect(Collectors.toMap(OrderUnit::getId, Function.identity()));

		List<Long> missingIds = ids.stream().filter(id -> !orderUnitMap.containsKey(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Order Unit with IDs " + missingIds + " not found.");
		}

		// Return the list of order units
		return orderUnits;
	}

}
