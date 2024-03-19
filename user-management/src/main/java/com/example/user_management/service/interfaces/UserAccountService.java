package com.example.user_management.service.interfaces;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.exceptions.ResourceNotFoundException;

public interface UserAccountService {
	void save(UserResponse user, String token);

	List<UserAccountRequest> findAll();

	void deleteById(@NonNull Long id);

	UserAccountRequest findByToken(String token) throws ResourceNotFoundException;

	UserAccountRequest findById(@NonNull Long id) throws ResourceNotFoundException;

	List<UserAccountRequest> findExpiredTokens(long currentTimestamp);
}
