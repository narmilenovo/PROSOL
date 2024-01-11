package com.example.requestitemservice.service;

import java.util.Comparator;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.requestitemservice.client.MaterialItem;
import com.example.requestitemservice.client.general.GeneralServiceClient;
import com.example.requestitemservice.client.general.IndustrySectorResponse;
import com.example.requestitemservice.client.general.MaterialTypeResponse;
import com.example.requestitemservice.client.plant.PlantResponse;
import com.example.requestitemservice.client.plant.PlantServiceClient;
import com.example.requestitemservice.client.plant.StorageLocationResponse;
import com.example.requestitemservice.client.salesothers.MaterialStrategicGroupResponse;
import com.example.requestitemservice.client.salesothers.SalesServiceClient;
import com.example.requestitemservice.dto.request.RequestItemRequest;
import com.example.requestitemservice.dto.response.RequestItemResponse;
import com.example.requestitemservice.entity.RequestItem;
import com.example.requestitemservice.repository.RequestItemRepository;
import com.example.requestitemservice.service.interfaces.RequestItemService;
import com.example.requestitemservice.utils.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestItemServiceImpl implements RequestItemService {

	private final RequestItemRepository requestItemRepository;
	private final ModelMapper modelMapper;
	private final FileUploadUtil fileUploadUtil;
	private final PlantServiceClient plantClient;
	private final GeneralServiceClient generalClient;
	private final SalesServiceClient salesClient;

	@Override
	public RequestItemResponse save(RequestItemRequest requestItemRequest, MultipartFile file) {
		RequestItem requestItem = modelMapper.map(requestItemRequest, RequestItem.class);
		requestItem.setId(null);
		RequestItem saveEmptyRequestId = requestItemRepository.save(requestItem);

		String fileName = fileUploadUtil.storeFile(file, saveEmptyRequestId.getId());
		saveEmptyRequestId.setAttachment(fileName);

		RequestItem savedRequestItem = requestItemRepository.save(saveEmptyRequestId);
		return mapToRequestItemResponse(savedRequestItem);
	}

	@Override
	public RequestItemResponse getRequestItem(Long id) {
		RequestItem requestItem = this.getRequestItemById(id);
		return mapToRequestItemResponse(requestItem);
	}

	@Override
	public MaterialItem getMaterialItem(Long id) {
		RequestItem requestItem = this.getRequestItemById(id);
		return mapToMaterialItem(requestItem);
	}

	@Override
	public List<RequestItemResponse> getAllRequestItem() {
		List<RequestItem> requestItems = requestItemRepository.findAll();
		return requestItems.stream().sorted(Comparator.comparing(RequestItem::getId))
				.map(this::mapToRequestItemResponse).toList();
	}

	@Override
	public List<MaterialItem> getAllMaterialItem() {
		List<RequestItem> requestItems = requestItemRepository.findAll();
		return requestItems.stream().sorted(Comparator.comparing(RequestItem::getId)).map(this::mapToMaterialItem)
				.toList();
	}

	@Override
	public RequestItemResponse update(Long id, RequestItemRequest updatedItem, MultipartFile file) {
		RequestItem existingRequestItem = this.getRequestItemById(id);
		modelMapper.map(existingRequestItem, updatedItem);
		existingRequestItem.setId(id);

		String existingFile = existingRequestItem.getAttachment();
		fileUploadUtil.deleteFile(existingFile, id);
		String newFile = fileUploadUtil.storeFile(file, id);
		existingRequestItem.setAttachment(newFile);

		RequestItem updatedRequestItem = requestItemRepository.save(existingRequestItem);
		return mapToRequestItemResponse(updatedRequestItem);
	}

	@Override
	public void delete(Long id) {
		RequestItem requestItem = this.getRequestItemById(id);
		fileUploadUtil.deleteDir(requestItem.getAttachment(), id);
		requestItemRepository.delete(requestItem);
	}

	@Override
	public void deleteBatchRequest(List<Long> ids) {
		requestItemRepository.deleteAllByIdInBatch(ids);
	}

	private RequestItem getRequestItemById(Long id) {
		return requestItemRepository.findById(id).orElseThrow(() -> new RuntimeException("RequestItem not found"));

	}

	private RequestItemResponse mapToRequestItemResponse(RequestItem requestItem) {
		return modelMapper.map(requestItem, RequestItemResponse.class);
	}

	private MaterialItem mapToMaterialItem(RequestItem requestItem) {
		MaterialItem materialItem = modelMapper.map(requestItem, MaterialItem.class);
		// Plant Client
		PlantResponse plant = plantClient.getPlantById(requestItem.getPlantId());
		materialItem.setPlant(plant);
		// Storage Location By Id
		StorageLocationResponse storageLocation = plantClient
				.getStorageLocationById(requestItem.getStorageLocationId());
		materialItem.setStorageLocation(storageLocation);
		// Material Type
		MaterialTypeResponse materialType = generalClient.getMaterialById(requestItem.getMaterialTypeId());
		materialItem.setMaterialType(materialType);
		// Industry Sector
		IndustrySectorResponse industrySector = generalClient.getSectorById(requestItem.getIndustrySectorId());
		materialItem.setIndustrySector(industrySector);
		// Material Strategic Group
		MaterialStrategicGroupResponse materialGroup = salesClient.getMsgById(requestItem.getMaterialGroupId());
		materialItem.setMaterialGroup(materialGroup);
		return materialItem;
	}

}
