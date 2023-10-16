package com.example.sales_otherservice.service.interfaces;

import com.example.sales_otherservice.dto.request.ItemCategoryGroupRequest;
import com.example.sales_otherservice.dto.response.ItemCategoryGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

import java.util.List;

public interface ItemCategoryGroupService {
    ItemCategoryGroupResponse saveIcg(ItemCategoryGroupRequest itemCategoryGroupRequest) throws ResourceFoundException;

    List<ItemCategoryGroupResponse> getAllIcg();

    ItemCategoryGroupResponse getIcgById(Long id) throws ResourceNotFoundException;

    List<ItemCategoryGroupResponse> findAllStatusTrue();

    ItemCategoryGroupResponse updateIcg(Long id, ItemCategoryGroupRequest updateItemCategoryGroupRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deleteIcgById(Long id) throws ResourceNotFoundException;
}
