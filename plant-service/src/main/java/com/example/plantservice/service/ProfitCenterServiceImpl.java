package com.example.plantservice.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.ProfitCenterRequest;
import com.example.plantservice.dto.response.ProfitCenterResponse;
import com.example.plantservice.entity.Plant;
import com.example.plantservice.entity.ProfitCenter;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
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
	private static final String PROFIT_CENTER_NOT_FOUND_MESSAGE = null;

	private final ProfitCenterRepo profitCenterRepo;

	private final PlantRepo plantRepo;
	private final ExcelFileHelper excelFileHelper;
	private final DynamicClient dynamicClient;
	private final ModelMapper modelMapper;

	@Override
	public ProfitCenterResponse saveProfitCenter(ProfitCenterRequest profitCenterRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = profitCenterRepo.existsByProfitCenterCodeAndProfitCenterName(
				profitCenterRequest.getProfitCenterCode(), profitCenterRequest.getProfitCenterName());
		if (!exists) {
			ProfitCenter profitCenter = modelMapper.map(profitCenterRequest, ProfitCenter.class);
			profitCenter.setId(null);
			Plant plant = this.getPlantById(profitCenterRequest.getPlantId());
			profitCenter.setPlant(plant);
			for (Map.Entry<String, Object> entryField : profitCenterRequest.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ProfitCenter.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			ProfitCenter savedProfitCenter = profitCenterRepo.save(profitCenter);
			return this.mapToProfitCenterResponse(savedProfitCenter);
		} else {
			throw new AlreadyExistsException("ProfitCenter with this name already exists");
		}
	}

	@Override
	public ProfitCenterResponse getProfitCenterById(Long id) throws ResourceNotFoundException {
		ProfitCenter profitCenter = this.findProfitCenterById(id);
		return mapToProfitCenterResponse(profitCenter);
	}

	@Override
	public List<ProfitCenterResponse> getAllProfitCenter() {
		List<ProfitCenter> profitCenter = profitCenterRepo.findAllByOrderByIdAsc();
		return profitCenter.stream().map(this::mapToProfitCenterResponse).toList();
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
		String existName = profitCenterRequest.getProfitCenterName();
		String existCode = profitCenterRequest.getProfitCenterCode();
		boolean exists = profitCenterRepo.existsByProfitCenterCodeAndProfitCenterNameAndIdNot(existCode, existName, id);
		if (!exists) {
			ProfitCenter existingProfitCenter = this.findProfitCenterById(id);
			existingProfitCenter.setProfitCenterCode(profitCenterRequest.getProfitCenterCode());
			existingProfitCenter.setProfitCenterName(profitCenterRequest.getProfitCenterName());
			existingProfitCenter.setProfitCenterStatus(profitCenterRequest.getProfitCenterStatus());
			Plant existingPlant = existingProfitCenter.getPlant();
			if (profitCenterRequest.getPlantId() != null) {
				existingPlant = this.getPlantById(profitCenterRequest.getPlantId());
				existingProfitCenter.setPlant(existingPlant);
			}
			for (Map.Entry<String, Object> entryField : existingProfitCenter.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = ProfitCenter.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			existingProfitCenter.setDynamicFields(profitCenterRequest.getDynamicFields());
			ProfitCenter savedProfitCenter = profitCenterRepo.save(existingProfitCenter);
			return this.mapToProfitCenterResponse(savedProfitCenter);
		} else {
			throw new AlreadyExistsException("ProfitCenter with this name already exists");

		}

	}

	@Override
	public List<ProfitCenterResponse> updateBulkStatusProfitCenterId(List<Long> id) throws ResourceNotFoundException {
		List<ProfitCenter> existingProfitCenter = this.findAllProfitCenterById(id);
		for (ProfitCenter profitCenter : existingProfitCenter) {
			profitCenter.setProfitCenterStatus(!profitCenter.getProfitCenterStatus());
		}
		profitCenterRepo.saveAll(existingProfitCenter);
		return existingProfitCenter.stream().map(this::mapToProfitCenterResponse).toList();
	}

	@Override
	public ProfitCenterResponse updateStatusUsingProfitCenterId(Long id) throws ResourceNotFoundException {
		ProfitCenter existingProfitCenter = this.findProfitCenterById(id);
		existingProfitCenter.setProfitCenterStatus(!existingProfitCenter.getProfitCenterStatus());
		profitCenterRepo.save(existingProfitCenter);
		return mapToProfitCenterResponse(existingProfitCenter);
	}

	@Override
	public void deleteProfitCenter(Long id) throws ResourceNotFoundException {
		ProfitCenter profitCenter = this.findProfitCenterById(id);
		profitCenterRepo.deleteById(profitCenter.getId());
	}

	@Override
	public void deleteBatchProfitCenter(List<Long> ids) throws ResourceNotFoundException {
		this.findAllProfitCenterById(ids);
		profitCenterRepo.deleteAllByIdInBatch(ids);
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

	private ProfitCenterResponse mapToProfitCenterResponse(ProfitCenter profitCenter) {
		ProfitCenterResponse profitCenterResponse = modelMapper.map(profitCenter, ProfitCenterResponse.class);
		return profitCenterResponse;
	}

	private Plant getPlantById(Long id) throws ResourceNotFoundException {
		Optional<Plant> plant = plantRepo.findById(id);
		return plant.orElseThrow(() -> new ResourceNotFoundException("Plant with id " + id + " not found"));
	}

	private ProfitCenter findProfitCenterById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<ProfitCenter> profitCenter = profitCenterRepo.findById(id);
		if (profitCenter.isEmpty()) {
			throw new ResourceNotFoundException(PROFIT_CENTER_NOT_FOUND_MESSAGE);
		}
		return profitCenter.get();
	}

	private List<ProfitCenter> findAllProfitCenterById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<ProfitCenter> profitCenters = profitCenterRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> profitCenters.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Profit Center with IDs " + missingIds + " not found.");
		}
		return profitCenters;
	}
}
