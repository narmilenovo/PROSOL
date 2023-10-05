package com.example.user_management.service;

import com.example.user_management.dto.request.LoginRequest;
import com.example.user_management.dto.request.TokenRequest;
import com.example.user_management.dto.request.TokenType;
import com.example.user_management.dto.response.AuthenticationResponse;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.Token;
import com.example.user_management.entity.User;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.repository.TokenRepository;
import com.example.user_management.repository.UserRepository;
import com.example.user_management.security.CustomUserDetails;
import com.example.user_management.service.interfaces.AuthenticationService;
import com.example.user_management.service.interfaces.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import static com.example.user_management.utils.Constants.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final ModelMapper modelMapper;
    private final AuthenticationManager authenticationManager;


    @Override
    public AuthenticationResponse authenticate(LoginRequest loginRequest) throws ResourceNotFoundException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        UserResponse userResponse = userService.findByEmail(loginRequest.getEmail());
        User user = modelMapper.map(userResponse, User.class);
        UserDetails userDetails = new CustomUserDetails(user);
        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        revokeAllUserTokens(userResponse);
        saveUserToken(userResponse, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ResourceNotFoundException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return;
        }
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            UserResponse userResponse = userService.findByEmail(userEmail);
            User user = modelMapper.map(userResponse, User.class);
            UserDetails userDetails = new CustomUserDetails(user);
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                var accessToken = jwtService.generateToken(userDetails);
                revokeAllUserTokens(userResponse);
                saveUserToken(userResponse, accessToken);
                Date expirationDate = jwtService.extractExpiration(accessToken);
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .expiresAt(expirationDate)
                        .build();
                response.setContentType(APPLICATION_JSON);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    @Override
    public UserResponse validation(String token) {
        Optional<User> user = userRepository.findByEmail(jwtService.extractUsername(token));
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(NO_USER_FOUND_WITH_EMAIL_MESSAGE);
        }
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public void revokeAllUserTokens(UserResponse userResponse) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(userResponse.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
        var nonValidUserTokens = tokenRepository.findAllNonValidTokenByUser(userResponse.getId());
        tokenRepository.deleteAll(nonValidUserTokens);


    }

    @Override
    public void saveUserToken(UserResponse userResponse, String jwtToken) {
        var tokenRequest = TokenRequest.builder()
                .user(userResponse)
                .tokenValue(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        Token token = modelMapper.map(tokenRequest, Token.class);
        tokenRepository.save(token);
    }
}
