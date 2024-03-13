package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.ProfitCenterRequest;
import com.example.plantservice.dto.response.ProfitCenterResponse;
import com.example.plantservice.entity.AuditFields;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.ProfitCenter;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
import com.example.plantservice.mapping.ProfitCenterMapper;
import com.example.plantservice.repository.PlantRepo;
import com.example.plantservice.repository.ProfitCenterRepo;
import com.example.plantservice.service.interfaces.ProfitCenterService;
import com.example.plantservice.util.ExcelFileHelper;
import com.example.plantservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfitCenterServiceImpl implements ProfitCenterService {

	private final ProfitCenterRepo profitCenterRepo;
	private final PlantRepo plantRepo;
	private final ExcelFileHelper excelFileHelper;
	private final DynamicClient dynamicClient;
	private final ProfitCenterMapper profitCenterMapper;

	@Override
	public ProfitCenterResponse saveProfitCenter(ProfitCenterRequest profitCenterRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		Helpers.inputTitleCase(profitCenterRequest);
		String profitCenterCode = profitCenterRequest.getProfitCenterCode();
		String profitCenterName = profitCenterRequest.getProfitCenterName();
		if (profitCenterRepo.existsByProfitCenterCodeAndProfitCenterName(profitCenterCode, profitCenterName)) {
			throw new AlreadyExistsException("ProfitCenter with this name already exists");
		}
		ProfitCenter profitCenter = profitCenterMapper.mapToProfitCenter(profitCenterRequest);
		profitCenter.setId(null);
		Plant plant = this.getPlantById(profitCenterRequest.getPlantId());
		profitCenter.setPlant(plant);

		validateDynamicFields(profitCenter);

		ProfitCenter savedProfitCenter = profitCenterRepo.save(profitCenter);
		return profitCenterMapper.mapToProfitCenterResponse(savedProfitCenter);
	}

	@Override
	public ProfitCenterResponse getProfitCenterById(Long id) throws ResourceNotFoundException {
		ProfitCenter profitCenter = this.findProfitCenterById(id);
		return profitCenterMapper.mapToProfitCenterResponse(profitCenter);
	}

	@Override
	public List<ProfitCenterResponse> getAllProfitCenter() {
		List<ProfitCenter> profitCenter = profitCenterRepo.findAllByOrderByIdAsc();
		return profitCenter.stream().map(profitCenterMapper::mapToProfitCenterResponse).toList();
	}

	@Override
	public List<ProfitCenter> findAll() {
		return profitCenterRepo.findAllByOrderByIdAsc();
	}

	@Override
	@Transactional
	public ProfitCenterResponse updateProfitCenter(Long id, ProfitCenterRequest profitCenterRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		Helpers.inputTitleCase(profitCenterRequest);
		String existName = profitCenterRequest.getProfitCenterName();
		String existCode = profitCenterRequest.getProfitCenterCode();
		boolean exists = profitCenterRepo.existsByProfitCenterCodeAndProfitCenterNameAndIdNot(existCode, existName, id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (!exists) {
			ProfitCenter existingProfitCenter = this.findProfitCenterById(id);
			if (!existingProfitCenter.getProfitCenterCode().equals(existCode)) {
				auditFields.add(new AuditFields(null, "ProfitCenter Code", existingProfitCenter.getProfitCenterCode(),
						existCode));
				existingProfitCenter.setProfitCenterCode(existCode);
			}
			if (!existingProfitCenter.getProfitCenterName().equals(existName)) {
				auditFields.add(new AuditFields(null, "ProfitCenter Name", existingProfitCenter.getProfitCenterName(),
						existName));
				existingProfitCenter.setProfitCenterName(existName);
			}
			if (!existingProfitCenter.getProfitCenterStatus().equals(profitCenterRequest.getProfitCenterStatus())) {
				auditFields.add(new AuditFields(null, "ProfitCenter Status",
						existingProfitCenter.getProfitCenterStatus(), profitCenterRequest.getProfitCenterStatus()));
				existingProfitCenter.setProfitCenterStatus(profitCenterRequest.getProfitCenterStatus());
			}
			Plant existingPlant = existingProfitCenter.getPlant();
			if (!existingPlant.equals(getPlantById(profitCenterRequest.getPlantId()))) {
				existingPlant = this.getPlantById(profitCenterRequest.getPlantId());
				auditFields.add(new AuditFields(null, "Plants", existingPlant, profitCenterRequest.getPlantId()));
				existingProfitCenter.setPlant(existingPlant);
			}
			if (!existingProfitCenter.getDynamicFields().equals(profitCenterRequest.getDynamicFields())) {
				for (Map.Entry<String, Object> entry : profitCenterRequest.getDynamicFields().entrySet()) {
					String fieldName = entry.getKey();
					Object newValue = entry.getValue();
					Object oldValue = existingProfitCenter.getDynamicFields().get(fieldName);
					if (oldValue == null || !oldValue.equals(newValue)) {
						auditFields.add(new AuditFields(null, fieldName, oldValue, newValue));
						existingProfitCenter.getDynamicFields().put(fieldName, newValue); // Update the dynamic field
					}
				}
			}
			existingProfitCenter.updateAuditHistory(auditFields);
			ProfitCenter savedProfitCenter = profitCenterRepo.save(existingProfitCenter);
			return profitCenterMapper.mapToProfitCenterResponse(savedProfitCenter);
		} else {
			throw new AlreadyExistsException("ProfitCenter with this name already exists");

		}

	}

	@Override
	public List<ProfitCenterResponse> updateBulkStatusProfitCenterId(List<Long> id) throws ResourceNotFoundException {
		List<ProfitCenter> existingProfitCenters = this.findAllProfitCenterById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		existingProfitCenters.forEach(existingProfitCenter -> {
			if (existingProfitCenter.getProfitCenterStatus() != null) {
				auditFields.add(new AuditFields(null, "ProfitCenter Status",
						existingProfitCenter.getProfitCenterStatus(), !existingProfitCenter.getProfitCenterStatus()));
				existingProfitCenter.setProfitCenterStatus(!existingProfitCenter.getProfitCenterStatus());
			}
			existingProfitCenter.updateAuditHistory(auditFields);
		});
		profitCenterRepo.saveAll(existingProfitCenters);
		return existingProfitCenters.stream().map(profitCenterMapper::mapToProfitCenterResponse).toList();
	}

	@Override
	public ProfitCenterResponse updateStatusUsingProfitCenterId(Long id) throws ResourceNotFoundException {
		ProfitCenter existingProfitCenter = this.findProfitCenterById(id);
		// Find properties that have changed
		List<AuditFields> auditFields = new ArrayList<>();
		if (existingProfitCenter.getProfitCenterStatus() != null) {
			auditFields.add(new AuditFields(null, "ProfitCenter Status", existingProfitCenter.getProfitCenterStatus(),
					!existingProfitCenter.getProfitCenterStatus()));
			existingProfitCenter.setProfitCenterStatus(!existingProfitCenter.getProfitCenterStatus());
		}
		existingProfitCenter.updateAuditHistory(auditFields);
		profitCenterRepo.save(existingProfitCenter);
		return profitCenterMapper.mapToProfitCenterResponse(existingProfitCenter);
	}

	@Override
	public void deleteProfitCenter(Long id) throws ResourceNotFoundException {
		ProfitCenter profitCenter = this.findProfitCenterById(id);
		if (profitCenter != null) {
			profitCenterRepo.delete(profitCenter);
		}
	}

	@Override
	public void deleteBatchProfitCenter(List<Long> ids) throws ResourceNotFoundException {
		List<ProfitCenter> profitCenters = this.findAllProfitCenterById(ids);
		if (!profitCenters.isEmpty()) {
			profitCenterRepo.deleteAll(profitCenters);
		}
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "ProfitCenter";
		Class<?> clazz = ProfitCenterRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ProfitCenter_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<ProfitCenter> data = excelFileHelper.readDataFromExcel(file.getInputStream(), ProfitCenter.class);
		for (ProfitCenter dataS : data) {
			if (!profitCenterRepo.existsByProfitCenterCodeAndProfitCenterName(dataS.getProfitCenterCode(),
					dataS.getProfitCenterName())) {

				this.profitCenterRepo.save(dataS);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "ProfitCenter";
		Class<?> clazz = ProfitCenterResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "ProfitCenter_";
		List<ProfitCenterResponse> allValue = getAllProfitCenter();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertProfitListToMap(List<ProfitCenter> profitList) {
		List<Map<String, Object>> profits = new ArrayList<>();

		for (ProfitCenter profit : profitList) {
			Map<String, Object> profitData = new HashMap<>();
			profitData.put("Id", profit.getId());
			profitData.put("Code", profit.getProfitCenterCode());
			profitData.put("Name", profit.getProfitCenterName());
			profitData.put("Status", profit.getProfitCenterStatus());
			profits.add(profitData);
		}
		return profits;
	}

	private void validateDynamicFields(ProfitCenter profitCenter) throws ResourceNotFoundException {
		for (Map.Entry<String, Object> entryField : profitCenter.getDynamicFields().entrySet()) {
			String fieldName = entryField.getKey();
			String formName = ProfitCenter.class.getSimpleName();
			boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
			if (!fieldExists) {
				throw new ResourceNotFoundException("Field of '" + fieldName
						+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
			}
		}
	}

	private Plant getPlantById(Long id) throws ResourceNotFoundException {
		return plantRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Plant with id " + id + " not found"));
	}

	private ProfitCenter findProfitCenterById(Long id) throws ResourceNotFoundException {
		return profitCenterRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Profit Center with ID " + id + " not found"));
	}

	private List<ProfitCenter> findAllProfitCenterById(List<Long> ids) throws ResourceNotFoundException {
		Set<Long> idSet = new HashSet<>(ids);
		List<ProfitCenter> profitCenters = profitCenterRepo.findAllById(ids);
		List<ProfitCenter> foundProfitCenters = profitCenters.stream().filter(entity -> idSet.contains(entity.getId()))
				.toList();

		List<Long> missingIds = ids.stream().filter(id -> !idSet.contains(id)).toList();

		if (!missingIds.isEmpty()) {
			throw new ResourceNotFoundException("Profit Center with IDs " + missingIds + " not found.");
		}
		return foundProfitCenters;
	}

}
