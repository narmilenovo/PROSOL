package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.ItemCategoryGroupRequest;
import com.example.sales_otherservice.dto.response.ItemCategoryGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface ItemCategoryGroupService {
	ItemCategoryGroupResponse saveIcg(ItemCategoryGroupRequest itemCategoryGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<ItemCategoryGroupResponse> getAllIcg();

	ItemCategoryGroupResponse getIcgById(@NonNull Long id) throws ResourceNotFoundException;

	List<ItemCategoryGroupResponse> findAllStatusTrue();

	ItemCategoryGroupResponse updateIcg(@NonNull Long id, ItemCategoryGroupRequest updateItemCategoryGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteIcgById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchIcg(@NonNull List<Long> ids) throws ResourceNotFoundException;

	ItemCategoryGroupResponse updateIcgStatus(@NonNull Long id) throws ResourceNotFoundException;

	List<ItemCategoryGroupResponse> updateBatchIcgStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;
}
