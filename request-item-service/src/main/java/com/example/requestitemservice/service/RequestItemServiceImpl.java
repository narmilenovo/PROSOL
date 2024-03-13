package com.example.requestitemservice.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.springframework.lang.NonNull;
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
import com.example.requestitemservice.entity.AuditFields;
import com.example.requestitemservice.entity.RequestItem;
import com.example.requestitemservice.mapping.RequestItemMapper;
import com.example.requestitemservice.repository.RequestItemRepository;
import com.example.requestitemservice.service.interfaces.RequestItemService;
import com.example.requestitemservice.utils.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestItemServiceImpl implements RequestItemService {

	private final RequestItemRepository requestItemRepository;
	private final RequestItemMapper requestItemMapper;
	private final FileUploadUtil fileUploadUtil;
	private final PlantServiceClient plantClient;
	private final GeneralServiceClient generalClient;
	private final SalesServiceClient salesClient;

	@Override
	public RequestItemResponse save(RequestItemRequest requestItemRequest, MultipartFile file) {
		RequestItem requestItem = requestItemMapper.mapToRequestItem(requestItemRequest);
		requestItem.setId(null);
		RequestItem saveEmptyRequestId = requestItemRepository.save(requestItem);

		String fileName = fileUploadUtil.storeFile(file, saveEmptyRequestId.getId());
		saveEmptyRequestId.setAttachment(fileName);

		RequestItem savedRequestItem = requestItemRepository.save(saveEmptyRequestId);
		return requestItemMapper.mapToRequestItemResponse(savedRequestItem);
	}

	@Override
	public RequestItemResponse getRequestItem(@NonNull Long id) {
		RequestItem requestItem = this.getRequestItemById(id);
		return requestItemMapper.mapToRequestItemResponse(requestItem);
	}

	@Override
	public MaterialItem getMaterialItem(@NonNull Long id) {
		RequestItem requestItem = this.getRequestItemById(id);
		return mapToMaterialItem(requestItem);
	}

	@Override
	public List<RequestItemResponse> getAllRequestItem() {
		List<RequestItem> requestItems = requestItemRepository.findAll();
		return requestItems.stream().sorted(Comparator.comparing(RequestItem::getId))
				.map(requestItemMapper::mapToRequestItemResponse).toList();
	}

	@Override
	public List<MaterialItem> getAllMaterialItem() {
		List<RequestItem> requestItems = requestItemRepository.findAll();
		return requestItems.stream().sorted(Comparator.comparing(RequestItem::getId)).map(this::mapToMaterialItem)
				.toList();
	}

	@Override
	public RequestItemResponse update(@NonNull Long id, RequestItemRequest updatedItem, MultipartFile file) {
		RequestItem existingRequestItem = this.getRequestItemById(id);

		List<AuditFields> auditFields = new ArrayList<>();

		if (!Objects.equals(existingRequestItem.getPlantId(), updatedItem.getPlantId())) {
			auditFields.add(new AuditFields(null, "Plant", existingRequestItem.getPlantId(), updatedItem.getPlantId()));
			existingRequestItem.setPlantId(updatedItem.getPlantId());
		}
		if (!Objects.equals(existingRequestItem.getStorageLocationId(), updatedItem.getStorageLocationId())) {
			auditFields.add(new AuditFields(null, "Storage Location", existingRequestItem.getStorageLocationId(),
					updatedItem.getStorageLocationId()));
			existingRequestItem.setStorageLocationId((updatedItem.getStorageLocationId()));
		}
		if (!Objects.equals(existingRequestItem.getMaterialTypeId(), updatedItem.getMaterialTypeId())) {
			auditFields.add(new AuditFields(null, "Material Type", existingRequestItem.getMaterialTypeId(),
					updatedItem.getMaterialTypeId()));
			existingRequestItem.setMaterialTypeId((updatedItem.getMaterialTypeId()));
		}
		if (!Objects.equals(existingRequestItem.getIndustrySectorId(), updatedItem.getIndustrySectorId())) {
			auditFields.add(new AuditFields(null, "Industry Sector", existingRequestItem.getIndustrySectorId(),
					updatedItem.getIndustrySectorId()));
			existingRequestItem.setIndustrySectorId((updatedItem.getIndustrySectorId()));
		}
		if (!Objects.equals(existingRequestItem.getMaterialGroupId(), updatedItem.getMaterialGroupId())) {
			auditFields.add(new AuditFields(null, "Material Group", existingRequestItem.getMaterialGroupId(),
					updatedItem.getMaterialGroupId()));
			existingRequestItem.setMaterialGroupId((updatedItem.getMaterialGroupId()));
		}
		if (!Objects.equals(existingRequestItem.getSource(), updatedItem.getSource())) {
			auditFields.add(new AuditFields(null, "Source", existingRequestItem.getSource(), updatedItem.getSource()));
			existingRequestItem.setSource((updatedItem.getSource()));
		}
		String existingFile = existingRequestItem.getAttachment();
		if (!Objects.equals(existingFile, updatedItem.getAttachment())) {
			fileUploadUtil.deleteFile(existingFile, id);
			String newFile = fileUploadUtil.storeFile(file, id);
			auditFields.add(new AuditFields(null, "Attachment", existingFile, newFile));
			existingRequestItem.setAttachment(newFile);
		}
		existingRequestItem.updateAuditHistory(auditFields);
		RequestItem updatedRequestItem = requestItemRepository.save(existingRequestItem);
		return requestItemMapper.mapToRequestItemResponse(updatedRequestItem);
	}

	@Override
	public void delete(@NonNull Long id) {
		RequestItem requestItem = this.getRequestItemById(id);
		if (requestItem != null) {
			fileUploadUtil.deleteDir(requestItem.getAttachment(), id);
			requestItemRepository.delete(requestItem);
		}
	}

	@Override
	public void deleteBatchRequest(@NonNull List<Long> ids) {
		requestItemRepository.deleteAllByIdInBatch(ids);
	}

	private RequestItem getRequestItemById(@NonNull Long id) {
		return requestItemRepository.findById(id).orElseThrow(() -> new RuntimeException("RequestItem not found"));

	}

	private MaterialItem mapToMaterialItem(RequestItem requestItem) {
		MaterialItem materialItem = requestItemMapper.mapToMaterialItem(requestItem);
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
