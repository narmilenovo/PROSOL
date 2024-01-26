package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.ItemCategoryGroupRequest;
import com.example.sales_otherservice.dto.response.ItemCategoryGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface ItemCategoryGroupService {
	ItemCategoryGroupResponse saveIcg(ItemCategoryGroupRequest itemCategoryGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<ItemCategoryGroupResponse> getAllIcg();

	ItemCategoryGroupResponse getIcgById(Long id) throws ResourceNotFoundException;

	List<ItemCategoryGroupResponse> findAllStatusTrue();

	ItemCategoryGroupResponse updateIcg(Long id, ItemCategoryGroupRequest updateItemCategoryGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteIcgById(Long id) throws ResourceNotFoundException;

	void deleteBatchIcg(List<Long> ids) throws ResourceNotFoundException;

	ItemCategoryGroupResponse updateIcgStatus(Long id) throws ResourceNotFoundException;

	List<ItemCategoryGroupResponse> updateBatchIcgStatus(List<Long> ids) throws ResourceNotFoundException;
}
