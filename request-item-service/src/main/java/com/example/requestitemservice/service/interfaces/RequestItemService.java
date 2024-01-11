package com.example.requestitemservice.service.interfaces;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.requestitemservice.client.MaterialItem;
import com.example.requestitemservice.dto.request.RequestItemRequest;
import com.example.requestitemservice.dto.response.RequestItemResponse;

public interface RequestItemService {

	RequestItemResponse save(RequestItemRequest item, MultipartFile file);

	RequestItemResponse getRequestItem(Long id);

	List<RequestItemResponse> getAllRequestItem();

	RequestItemResponse update(Long id, RequestItemRequest updatedItem, MultipartFile file);

	void delete(Long id);

	MaterialItem getMaterialItem(Long id);

	List<MaterialItem> getAllMaterialItem();

	void deleteBatchRequest(List<Long> ids);

}
