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
import org.springframework.web.multipart.MultipartFile;

import com.example.plantservice.client.Dynamic.DynamicClient;
import com.example.plantservice.dto.request.PriceControlRequest;
import com.example.plantservice.dto.response.PriceControlResponse;
import com.example.plantservice.entity.PriceControl;
import com.example.plantservice.exception.AlreadyExistsException;
import com.example.plantservice.exception.ExcelFileException;
import com.example.plantservice.exception.ResourceNotFoundException;
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
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	public static final String PRICE_CONTROL_NOT_FOUND_MESSAGE = null;

	@Override
	public PriceControlResponse savePriceControl(PriceControlRequest priceControlRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = priceControlRepo.existsByPriceControlCodeAndPriceControlName(
				priceControlRequest.getPriceControlCode(), priceControlRequest.getPriceControlName());
		if (!exists) {
			PriceControl priceControl = modelMapper.map(priceControlRequest, PriceControl.class);
			for (Map.Entry<String, Object> entryField : priceControl.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = PriceControl.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			priceControlRepo.save(priceControl);
			return mapToPriceControlResponse(priceControl);
		} else {
			throw new AlreadyExistsException("PriceControl with this name already exists");
		}
	}

	@Override
	public PriceControlResponse getPriceControlById(Long id) throws ResourceNotFoundException {
		PriceControl pricecontrol = this.findPriceControlById(id);
		return mapToPriceControlResponse(pricecontrol);
	}

	@Override
	public List<PriceControlResponse> getAllPriceControl() {
		List<PriceControl> priceControl = priceControlRepo.findAllByOrderByIdAsc();
		return priceControl.stream().map(this::mapToPriceControlResponse).toList();
	}

	@Override
	public List<PriceControl> findAll() {
		return priceControlRepo.findAllByOrderByIdAsc();
	}

	@Override
	public PriceControlResponse updatePriceControl(Long id, PriceControlRequest priceControlRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String existName = priceControlRequest.getPriceControlName();
		String existCode = priceControlRequest.getPriceControlCode();
		boolean exists = priceControlRepo.existsByPriceControlCodeAndPriceControlNameAndIdNot(existCode, existName, id);
		if (!exists) {
			PriceControl existingPriceControl = this.findPriceControlById(id);
			modelMapper.map(priceControlRequest, existingPriceControl);
			for (Map.Entry<String, Object> entryField : existingPriceControl.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = PriceControl.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			priceControlRepo.save(existingPriceControl);
			return mapToPriceControlResponse(existingPriceControl);
		} else {
			throw new AlreadyExistsException("PriceControl with this name already exists");
		}
	}

	@Override
	public List<PriceControlResponse> updateBulkStatusPriceControlId(List<Long> id) throws ResourceNotFoundException {
		List<PriceControl> existingPriceControl = this.findAllPcById(id);
		for (PriceControl priceControl : existingPriceControl) {
			priceControl.setPriceControlStatus(!priceControl.getPriceControlStatus());
		}
		priceControlRepo.saveAll(existingPriceControl);
		return existingPriceControl.stream().map(this::mapToPriceControlResponse).toList();
	}

	@Override
	public PriceControlResponse updateStatusUsingPriceControlId(Long id) throws ResourceNotFoundException {
		PriceControl existingPriceControl = this.findPriceControlById(id);
		existingPriceControl.setPriceControlStatus(!existingPriceControl.getPriceControlStatus());
		priceControlRepo.save(existingPriceControl);
		return mapToPriceControlResponse(existingPriceControl);
	}

	@Override
	public void deletePriceControl(Long id) throws ResourceNotFoundException {
		PriceControl pricecontrol = this.findPriceControlById(id);
		priceControlRepo.deleteById(pricecontrol.getId());
	}

	@Override
	public void deleteBatchPriceControl(List<Long> ids) throws ResourceNotFoundException {
		this.findAllPcById(ids);
		priceControlRepo.deleteAllByIdInBatch(ids);
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

	private PriceControlResponse mapToPriceControlResponse(PriceControl pricecontrol) {
		return modelMapper.map(pricecontrol, PriceControlResponse.class);
	}

	private PriceControl findPriceControlById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<PriceControl> priceControl = priceControlRepo.findById(id);
		if (priceControl.isEmpty()) {
			throw new ResourceNotFoundException(PRICE_CONTROL_NOT_FOUND_MESSAGE);
		}
		return priceControl.get();
	}

	private List<PriceControl> findAllPcById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<PriceControl> priceControls = priceControlRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> priceControls.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Price Control with IDs " + missingIds + " not found.");
		}
		return priceControls;
	}

}
