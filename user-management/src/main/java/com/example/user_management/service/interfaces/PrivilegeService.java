package com.example.user_management.service.interfaces;

import java.util.List;

import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.response.PrivilegeResponse;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;

public interface PrivilegeService {
	PrivilegeResponse savePrivilege(PrivilegeRequest privilegeRequest) throws ResourceFoundException;

	PrivilegeResponse getPrivilegeById(Long id) throws ResourceNotFoundException;

	List<PrivilegeResponse> getAllPrivileges();

	PrivilegeResponse updatePrivilege(Long id, PrivilegeRequest updatePrivilegeRequest)
			throws ResourceNotFoundException, ResourceFoundException;

	PrivilegeResponse updateStatusUsingPrivilegeById(Long id) throws ResourceNotFoundException;

	List<PrivilegeResponse> updateBulkStatusPrivilegeById(List<Long> ids) throws ResourceNotFoundException;

	void deletePrivilege(Long id) throws ResourceNotFoundException;

	void deleteBatchPrivilege(List<Long> ids) throws ResourceNotFoundException;

}
