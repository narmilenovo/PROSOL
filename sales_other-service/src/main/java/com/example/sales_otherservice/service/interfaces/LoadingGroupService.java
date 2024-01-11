package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface LoadingGroupService {
	LoadingGroupResponse saveLg(LoadingGroupRequest loadingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<LoadingGroupResponse> getAllLg();

	LoadingGroupResponse getLgById(Long id) throws ResourceNotFoundException;

	List<LoadingGroupResponse> findAllStatusTrue();

	LoadingGroupResponse updateLg(Long id, LoadingGroupRequest updateLoadingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteLgById(Long id) throws ResourceNotFoundException;

	void deleteBatchLg(List<Long> ids);
}
