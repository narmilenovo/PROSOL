package com.example.user_management.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.response.PrivilegeResponse;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;

public interface PrivilegeService {
	PrivilegeResponse savePrivilege(PrivilegeRequest privilegeRequest) throws ResourceFoundException;

	List<PrivilegeResponse> saveAllPrivileges(List<PrivilegeRequest> privilegeRequests) throws ResourceFoundException;

	PrivilegeResponse getPrivilegeById(@NonNull Long id) throws ResourceNotFoundException;

	List<PrivilegeResponse> getAllPrivileges();

	PrivilegeResponse updatePrivilege(@NonNull Long id, PrivilegeRequest updatePrivilegeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	PrivilegeResponse updateStatusUsingPrivilegeById(@NonNull Long id) throws ResourceNotFoundException;

	List<PrivilegeResponse> updateBulkStatusPrivilegeById(@NonNull List<Long> ids) throws ResourceNotFoundException;

	void deletePrivilege(@NonNull Long id) throws ResourceNotFoundException;

	void deleteBatchPrivilege(@NonNull List<Long> ids) throws ResourceNotFoundException;

}
