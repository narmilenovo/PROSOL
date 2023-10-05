package com.example.user_management.service.interfaces;

import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.response.PrivilegeResponse;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;

import java.util.List;

public interface PrivilegeService {
    PrivilegeResponse savePrivilege(PrivilegeRequest privilegeRequest) throws ResourceFoundException;

    PrivilegeResponse getPrivilegeById(Long id) throws ResourceNotFoundException;

    List<PrivilegeResponse> getAllPrivileges();

    PrivilegeResponse updatePrivilege(Long id, PrivilegeRequest updatePrivilegeRequest) throws ResourceNotFoundException, ResourceFoundException;

    void deletePrivilege(Long id) throws ResourceNotFoundException;

}
