package com.example.requestitemservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.web.multipart.MultipartFile;

import com.example.requestitemservice.client.MaterialItem;
import com.example.requestitemservice.dto.request.RequestItemRequest;
import com.example.requestitemservice.dto.response.RequestItemResponse;

public interface RequestItemService {

	RequestItemResponse save(RequestItemRequest item, MultipartFile file);

	RequestItemResponse getRequestItem(@NonNull Long id);

	List<RequestItemResponse> getAllRequestItem();

	RequestItemResponse update(@NonNull Long id, RequestItemRequest updatedItem, MultipartFile file);

	void delete(@NonNull Long id);

	MaterialItem getMaterialItem(@NonNull Long id);

	List<MaterialItem> getAllMaterialItem();

	void deleteBatchRequest(@NonNull List<Long> ids);

}
