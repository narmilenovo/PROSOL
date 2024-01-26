package com.example.mrpdataservice.serviceimpl;

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

import com.example.mrpdataservice.client.Dynamic.DynamicClient;
import com.example.mrpdataservice.entity.LotSize;
import com.example.mrpdataservice.exception.AlreadyExistsException;
import com.example.mrpdataservice.exception.ExcelFileException;
import com.example.mrpdataservice.exception.ResourceNotFoundException;
import com.example.mrpdataservice.repository.LotSizeRepo;
import com.example.mrpdataservice.request.LotSizeRequest;
import com.example.mrpdataservice.response.LotSizeResponse;
import com.example.mrpdataservice.service.LotSizeService;
import com.example.mrpdataservice.util.ExcelFileHelper;
import com.example.mrpdataservice.util.Helpers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LotSizeServiceImpl implements LotSizeService {
	private final LotSizeRepo lotSizeRepo;
	private final ExcelFileHelper excelFileHelper;
	private final ModelMapper modelMapper;
	private final DynamicClient dynamicClient;

	public static final String LOT_SIZE_NOT_FOUND_MESSAGE = null;

	@Override
	public LotSizeResponse saveLotSize(LotSizeRequest lotSizeRequest)
			throws AlreadyExistsException, ResourceNotFoundException {
		boolean exists = lotSizeRepo.existsByLotSizeCodeAndLotSizeName(lotSizeRequest.getLotSizeCode(),
				lotSizeRequest.getLotSizeName());
		if (!exists) {
			LotSize lotSize = modelMapper.map(lotSizeRequest, LotSize.class);
			for (Map.Entry<String, Object> entryField : lotSize.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = LotSize.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			lotSizeRepo.save(lotSize);
			return mapToLotSizeResponse(lotSize);
		} else {
			throw new AlreadyExistsException("LotSize with this name already exists");
		}
	}

	@Override
	public LotSizeResponse getLotSizeById(Long id) throws ResourceNotFoundException {
		LotSize lotSize = this.findLotSizeById(id);
		return mapToLotSizeResponse(lotSize);
	}

	@Override
	public List<LotSizeResponse> getAllLotSize() {
		List<LotSize> lotSize = lotSizeRepo.findAllByOrderByIdAsc();
		return lotSize.stream().map(this::mapToLotSizeResponse).toList();
	}

	@Override
	public List<LotSize> findAll() {
		return lotSizeRepo.findAllByOrderByIdAsc();
	}

	@Override
	public LotSizeResponse updateLotSize(Long id, LotSizeRequest lotSizeRequest)
			throws ResourceNotFoundException, AlreadyExistsException {
		Helpers.validateId(id);
		String exist = lotSizeRequest.getLotSizeName();
		String existCode = lotSizeRequest.getLotSizeCode();
		boolean exists = lotSizeRepo.existsByLotSizeCodeAndLotSizeNameAndIdNot(existCode, exist, id);
		if (!exists) {
			LotSize existingLotSize = this.findLotSizeById(id);
			modelMapper.map(lotSizeRequest, existingLotSize);
			for (Map.Entry<String, Object> entryField : existingLotSize.getDynamicFields().entrySet()) {
				String fieldName = entryField.getKey();
				String formName = LotSize.class.getSimpleName();
				boolean fieldExists = dynamicClient.checkFieldNameInForm(fieldName, formName);
				if (!fieldExists) {
					throw new ResourceNotFoundException("Field of '" + fieldName
							+ "' not exist in Dynamic Field creation for form '" + formName + "' !!");
				}
			}
			lotSizeRepo.save(existingLotSize);
			return mapToLotSizeResponse(existingLotSize);

		} else {
			throw new AlreadyExistsException("LotSize with this name already exists");
		}
	}

	@Override
	public List<LotSizeResponse> updateBulkStatusLotSizeId(List<Long> id) throws ResourceNotFoundException {
		List<LotSize> existingLotSize = this.findAllLotSizeById(id);
		for (LotSize lotSize : existingLotSize) {
			lotSize.setLotSizeStatus(!lotSize.getLotSizeStatus());
		}
		lotSizeRepo.saveAll(existingLotSize);
		return existingLotSize.stream().map(this::mapToLotSizeResponse).toList();
	}

	@Override
	public LotSizeResponse updateStatusUsingLotSizeId(Long id) throws ResourceNotFoundException {
		LotSize existingLotSize = this.findLotSizeById(id);
		existingLotSize.setLotSizeStatus(!existingLotSize.getLotSizeStatus());
		lotSizeRepo.save(existingLotSize);
		return mapToLotSizeResponse(existingLotSize);
	}

	@Override
	public void deleteLotSize(Long id) throws ResourceNotFoundException {
		LotSize lotSize = this.findLotSizeById(id);
		lotSizeRepo.deleteById(lotSize.getId());
	}

	@Override
	public void deleteBatchLotSize(List<Long> ids) throws ResourceNotFoundException {
		this.findAllLotSizeById(ids);
		lotSizeRepo.deleteAllByIdInBatch(ids);
	}

	@Override
	public void downloadTemplate(HttpServletResponse response) throws IOException {
		String sheetName = "LotSize";
		Class<?> clazz = LotSizeRequest.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "LotSize_";
		excelFileHelper.exportTemplate(response, sheetName, clazz, contextType, extension, prefix);
	}

	@Override
	public void importExcelSave(MultipartFile file) throws AlreadyExistsException, IOException, ExcelFileException {
		List<LotSize> dataS = excelFileHelper.readDataFromExcel(file.getInputStream(), LotSize.class);
		for (LotSize data : dataS) {
			if (!lotSizeRepo.existsByLotSizeCodeAndLotSizeName(data.getLotSizeCode(), data.getLotSizeName())) {

				this.lotSizeRepo.save(data);
			} else {
				throw new AlreadyExistsException("Already SubMainGroup Name is Present");
			}
		}
	}

	@Override
	public void downloadAllData(HttpServletResponse response) throws IOException, ExcelFileException {
		String sheetName = "LotSize";
		Class<?> clazz = LotSizeResponse.class;
		String contextType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		String extension = ".xlsx";
		String prefix = "LotSize_";
		List<LotSizeResponse> allValue = getAllLotSize();
		excelFileHelper.exportData(response, sheetName, clazz, contextType, extension, prefix, allValue);
	}

	@Override
	public List<Map<String, Object>> convertLotSizeListToMap(List<LotSize> lotList) {
		List<Map<String, Object>> lotSize = new ArrayList<>();

		for (LotSize lotSizes : lotList) {
			Map<String, Object> lotSizeData = new HashMap<>();
			lotSizeData.put("Id", lotSizes.getId());
			lotSizeData.put("Name", lotSizes.getLotSizeName());
			lotSizeData.put("Status", lotSizes.getLotSizeStatus());
			lotSize.add(lotSizeData);
		}
		return lotSize;
	}

	private LotSizeResponse mapToLotSizeResponse(LotSize lotSize) {
		return modelMapper.map(lotSize, LotSizeResponse.class);
	}

	private LotSize findLotSizeById(Long id) throws ResourceNotFoundException {
		Helpers.validateId(id);
		Optional<LotSize> lotSize = lotSizeRepo.findById(id);
		if (lotSize.isEmpty()) {
			throw new ResourceNotFoundException(LOT_SIZE_NOT_FOUND_MESSAGE);
		}
		return lotSize.get();
	}

	private List<LotSize> findAllLotSizeById(List<Long> ids) throws ResourceNotFoundException {
		Helpers.validateIds(ids);
		List<LotSize> lotSizes = lotSizeRepo.findAllById(ids);
		// Check for missing IDs
		List<Long> missingIds = ids.stream()
				.filter(id -> lotSizes.stream().noneMatch(entity -> entity.getId().equals(id)))
				.collect(Collectors.toList());

		if (!missingIds.isEmpty()) {
			// Handle missing IDs, you can log a message or throw an exception
			throw new ResourceNotFoundException("Lot Sizes with IDs " + missingIds + " not found.");
		}
		return lotSizes;
	}

}
