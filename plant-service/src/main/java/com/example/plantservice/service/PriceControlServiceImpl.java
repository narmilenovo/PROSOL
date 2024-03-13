package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.PriceControlRequest;
import com.example.plantservice.dto.response.PriceControlResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.PriceControl;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.mapping.PriceControlMapper;
import com.example.plantservice.repository.PriceControlRepo;
import com.example.plantservice.service.interfaces.PriceControlService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PriceControlServiceImpl implements PriceControlService {

	private final PriceControlRepo priceControlRepo;
	private final ExcelFileHelper excelFileHelper;
	private final PriceControlMapper priceControlMapper;
	private final DynamicClient dynamicClient;

	@Override
	public PriceControlResponse savePriceControl(PriceControlRequest priceControlRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(priceControlRequest);
		String priceControlCode = priceControlRequest.getPriceControlCode();
		String priceControlName = priceControlRequest.getPriceControlName();
		if (priceControlRepo.existsByPriceControlCodeAndPriceControlName(priceControlCode, priceControlName)) {
			throw new AlreadyExistsException("PriceControl with this name already exists");
		}
		PriceControl priceControl = priceControlMapper.mapToPriceControl(priceControlRequest);
		validateDynamicFields(priceControl);
		priceControlRepo.save(priceControl);
		return priceControlMapper.mapToPriceControlResponse(priceControl);
	}

	@Override
	public PriceControlResponse getPriceControlById(Long id) throws ResourceNotFoundException {
		PriceControl pricecontrol = this.findPriceControlById(id);
		return priceControlMapper.mapToPriceControlResponse(pricecontrol);
	}

	@Override
	public List<PriceControlResponse> getAllPriceControl() {
		return priceControlRepo.findAllByOrderByIdAsc().stream().map(priceControlMapper::mapToPriceControlResponse)
				.toList();
	}

	@Override
	public List<PriceControl> findAll() {
		return priceControlRepo.findAllByOrderByIdAsc();
	}

	@Override
	public PriceControlResponse updatePriceControl(Long id, PriceControlRequest priceControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(priceControlRequest);
		String existName = priceControlRequest.getPriceControlName();
		String existCode = priceControlRequest.getPriceControlCode();
		boolean exists = priceControlRepo.existsByPriceControlCodeAndPriceControlNameAndIdNot(existCode, existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			PriceControl existingPriceControl = this.findPriceControlById(id);
			if (!existingPriceControl.getPriceControlCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "PriceControl Code", existingPriceControl.getPriceControlCode(),
						existCode));
				existingPriceControl.setPriceControlCode(existCode);
			}
			if (!existingPriceControl.getPriceControlName().equals(existName)) {
				auditFields.add(new AuditFields(null, "PriceControl Name", existingPriceControl.getPriceControlName(),
						existName));
				existingPriceControl.setPriceControlName(existName);
			}
			if (!existingPriceControl.getPriceControlStatus().equals(priceControlRequest.getPriceControlStatus())) {
				auditFields.add(new AuditFields(null, "PriceControl Status",
						existingPriceControl.getPriceControlStatus(), existingPriceControl.getPriceControlStatus()));
				existingPriceControl.setPriceControlStatus(priceControlRequest.getPriceControlStatus());
			}
			if (!existingPriceControl.getDynamicFields().equals(priceControlRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : priceControlRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingPriceControl.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingPriceControl.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingPriceControl.updateAuditHistory(auditFields);
			priceControlRepo.save(existingPriceControl);
			return priceControlMapper.mapToPriceControlResponse(existingPriceControl);
		} else {
			throw new AlreadyExistsException("PriceControl with this name already exists");
		}
	}

	@Override
	public List<PriceControlResponse> updateBulkStatusPriceControlId(List<Long> id) throws ResourceNotFoundException {
		List<PriceControl> existingPriceControls = this.findAllPcById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingPriceControls.forEach(existingPriceControl -> {
			if (existingPriceControl.getPriceControlStatus() != null) {
				auditFields.add(new AuditFields(null, "Price Control Status",
						existingPriceControl.getPriceControlStatus(), !existingPriceControl.getPriceControlStatus()));
				existingPriceControl.setPriceControlStatus(!existingPriceControl.getPriceControlStatus());
			}
			existingPriceControl.updateAuditHistory(auditFields);
		});
		priceControlRepo.saveAll(existingPriceControls);
		return existingPriceControls.stream().map(priceControlMapper::mapToPriceControlResponse).toList();
	}

	@Override
	public PriceControlResponse updateStatusUsingPriceControlId(Long id) throws ResourceNotFoundException {
		PriceControl existingPriceControl = this.findPriceControlById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingPriceControl.getPriceControlStatus() != null) {
			auditFields.add(new AuditFields(null, "Price Control Status", existingPriceControl.getPriceControlStatus(),
					!existingPriceControl.getPriceControlStatus()));
			existingPriceControl.setPriceControlStatus(!existingPriceControl.getPriceControlStatus());
		}
		existingPriceControl.updateAuditHistory(auditFields);
		priceControlRepo.save(existingPriceControl);
		return priceControlMapper.mapToPriceControlResponse(existingPriceControl);
	}

	@Override
	public void deletePriceControl(Long id) throws ResourceNotFoundException {
		PriceControl pricecontrol = this.findPriceControlById(id);
		if (pricecontrol != null) {
			priceControlRepo.delete(pricecontrol);
		}
	}

	@Override
	public void deleteBatchPriceControl(List<Long> ids) throws ResourceNotFoundException {
		List<PriceControl> priceControls = this.findAllPcById(ids);
		if (!priceControls.isEmpty()) {
			priceControlRepo.deleteAll(priceControls);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "PriceControl";
		Class<?> clazz = PriceControlRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "PriceControl_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<PriceControl> data = excelFileHelper.readDataFromExcel(file.getInputStream(), PriceControl.class);
		for (PriceControl priceControl : data) {
			if (!priceControlRepo.existsByPriceControlCodeAndPriceControlName(priceControl.getPriceControlCode(),
					priceControl.getPriceControlName())) {

				this.priceControlRepo.save(priceControl);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "PriceControl";
		Class<?> clazz = PriceControlResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "PriceControl_";
		List<PriceControlResponse> allValue = getAllPriceControl();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertPriceListToMap(List<PriceControl> priceList) {
		List<Map<String, Object>> prices = new ArrayList<>();

		for (PriceControl price : priceList) {
			Map<String, Object> priceData = new HashMap<>();
			priceData.put("Id", price.getId());
			priceData.put("Code", price.getPriceControlCode());
			priceData.put("Name", price.getPriceControlName());
			priceData.put("Status", price.getPriceControlStatus());
			prices.add(priceData);
		}
		return prices;
	}

	private void validateDynamicFields(PriceControl priceControl) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : priceControl.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = PriceControl.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private PriceControl findPriceControlById(Long id) throws ResourceNotFoundException {
		return priceControlRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Price Control with ID " + id + " not found"));
	}

	private List<PriceControl> findAllPcById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		Set<Long> idSet = new HashSet<>(ids);
		List<PriceControl> priceControls = priceControlRepo.findAllById(ids);
		List<PriceControl> foundPriceControls = priceControls.stream().filter(entity -> idSet.contains(entity.getId()))
				.toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Price Control with IDs " + missingIds + " not found.");
		}
		return foundPriceControls;
	}

}
