package com.example.sales_otherservice.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.sales_otherservice.dto.request.LoadingGroupRequest;
import com.example.sales_otherservice.dto.response.LoadingGroupResponse;
import com.example.sales_otherservice.exceptions.ResourceFoundException;
import com.example.sales_otherservice.exceptions.ResourceNotFoundException;

public interface LoadingGroupService {
	LoadingGroupResponse saveLg(LoadingGroupRequest loadingGroupRequest)
			throws ResourceFoundException, ResourceNotFoundException;

	List<LoadingGroupResponse> getAllLg();

	LoadingGroupResponse getLgById(@NonNull Long id) throws ResourceNotFoundException;

	List<LoadingGroupResponse> findAllStatusTrue();

	LoadingGroupResponse updateLg(@NonNull Long id, LoadingGroupRequest updateLoadingGroupRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	void deleteLgById(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchLg(@NonNull List<Long> ids) throws ResourceNotFoundException;

	List<LoadingGroupResponse> updateBatchLgStatus(@NonNull List<Long> ids) throws ResourceNotFoundException;

	LoadingGroupResponse updateLgStatus(@NonNull Long id) throws ResourceNotFoundException;
}
