package com.example.user_management.service.interfaces;

import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.exceptions.ResourceNotFoundException;

import java.util.List;

public interface UserAccountService {
    void save(UserResponse user, String token);

    List<UserAccountRequest> findAll();

    void delete(Long id);

    UserAccountRequest findByToken(String token) throws ResourceNotFoundException;

    UserAccountRequest findById(Long id) throws ResourceNotFoundException;

    List<UserAccountRequest> findExpiredTokens(long currentTimestamp);
}
