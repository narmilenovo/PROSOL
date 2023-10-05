package com.example.user_management.service.interfaces;

import com.example.user_management.dto.request.LoginRequest;
import com.example.user_management.dto.response.AuthenticationResponse;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {
    AuthenticationResponse authenticate(LoginRequest loginRequest) throws ResourceNotFoundException;

    void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ResourceNotFoundException;

    UserResponse validation(String token);

    void revokeAllUserTokens(UserResponse userResponse);

    void saveUserToken(UserResponse userResponse, String jwtToken);
}
