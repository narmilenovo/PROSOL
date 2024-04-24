package com.example.user_management.controller;

import static com.example.user_management.utils.Constants.ACCOUNT_DEACTIVATED_MESSAGE;
import static com.example.user_management.utils.Constants.DATA_KEY;
import static com.example.user_management.utils.Constants.INVALID_DATA_MESSAGE;
import static com.example.user_management.utils.Constants.INVALID_TOKEN_MESSAGE;
import static com.example.user_management.utils.Constants.JWT_EXPIRED_MESSAGE;
import static com.example.user_management.utils.Constants.JWT_ILLEGAL_ARGUMENT_MESSAGE;
import static com.example.user_management.utils.Constants.JWT_SIGNATURE_MESSAGE;
import static com.example.user_management.utils.Constants.MESSAGE_KEY;
import static com.example.user_management.utils.Constants.SWG_AUTH_LOGIN_ERROR;
import static com.example.user_management.utils.Constants.SWG_AUTH_LOGIN_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_AUTH_LOGIN_OPERATION;
import static com.example.user_management.utils.Constants.SWG_AUTH_LOGOUT_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_AUTH_LOGOUT_OPERATION;
import static com.example.user_management.utils.Constants.SWG_AUTH_TAG_DESCRIPTION;
import static com.example.user_management.utils.Constants.SWG_AUTH_TAG_NAME;
import static com.example.user_management.utils.Constants.SWG_REFRESH_TOKEN_OPERATION;
import static com.example.user_management.utils.Constants.SWG_TOKEN_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_TOKEN_REFRESH_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_TOKEN_VALIDATION_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_LOGGED_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_USER_LOGGED_OPERATION;
import static com.example.user_management.utils.Constants.TOKEN_NOT_FOUND_MESSAGE;
import static com.example.user_management.utils.Constants.UNAUTHORIZED_MESSAGE;
import static com.example.user_management.utils.Constants.VALIDATE_TOKEN_SUCCESS_MESSAGE;

import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.user_management.dto.request.LoginRequest;
import com.example.user_management.dto.response.AuthenticationResponse;
import com.example.user_management.dto.response.BadRequestResponse;
import com.example.user_management.dto.response.GenericResponse;
import com.example.user_management.dto.response.InvalidDataResponse;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.entity.User;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.mapping.UserMapper;
import com.example.user_management.security.CustomUserDetails;
import com.example.user_management.service.JwtService;
import com.example.user_management.service.LogoutService;
import com.example.user_management.service.interfaces.AuthenticationService;
import com.example.user_management.service.interfaces.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Tag(name = SWG_AUTH_TAG_NAME, description = SWG_AUTH_TAG_DESCRIPTION)
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
	private final AuthenticationService authenticationService;
	private final JwtService jwtService;
	private final UserMapper userMapper;
	private final UserService userService;
	private final LogoutService logoutService;
	private final AuthenticationManager authenticationManager;

	@Operation(summary = SWG_AUTH_LOGIN_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_AUTH_LOGIN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = AuthenticationResponse.class)) }),
			@ApiResponse(responseCode = "400", description = SWG_AUTH_LOGIN_ERROR, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping("/auth/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest)
			throws ResourceNotFoundException {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		UserResponse userResponse = userService.findByEmail(loginRequest.getEmail());
		Map<String, String> result = new HashMap<>();

		if (userResponse.getStatus().equals(false)) {
			result.put(DATA_KEY, ACCOUNT_DEACTIVATED_MESSAGE);
			return ResponseEntity.badRequest().body(result);
		}
		User user = userMapper.mapUserResponseToUser(userResponse);
		UserDetails userDetails = new CustomUserDetails(user);
		var jwtToken = jwtService.generateToken(userDetails);
		var refreshToken = jwtService.generateRefreshToken(userDetails);
		Date expirationDate = jwtService.extractExpiration(jwtToken);
		authenticationService.revokeAllUserTokens(userResponse);
		authenticationService.saveUserToken(userResponse, jwtToken);
		return ResponseEntity.ok(new AuthenticationResponse(jwtToken, refreshToken, expirationDate));
	}

	@Operation(summary = SWG_REFRESH_TOKEN_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_TOKEN_REFRESH_MESSAGE, content = {
					@Content(schema = @Schema(implementation = AuthenticationResponse.class)) }),
			@ApiResponse(responseCode = "400", description = SWG_AUTH_LOGIN_ERROR, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping(value = "/auth/refresh-token")
	public ResponseEntity<Object> refresh(@Valid @RequestParam String token) throws ResourceNotFoundException {
		String userEmail = jwtService.extractUsername(token);
		Map<String, String> result = new HashMap<>();
		if (token == null) {
			result.put(MESSAGE_KEY, INVALID_TOKEN_MESSAGE);
			return ResponseEntity.badRequest().body(result);
		}
		AuthenticationResponse authResponse = null;
		if (userEmail != null) {
			UserResponse userResponse = userService.findByEmail(userEmail);
			User user = userMapper.mapUserResponseToUser(userResponse);
			UserDetails userDetails = new CustomUserDetails(user);
			if (user == null) {
				result.put(MESSAGE_KEY, TOKEN_NOT_FOUND_MESSAGE);
				return ResponseEntity.badRequest().body(result);
			}
			if (jwtService.isTokenValid(token, userDetails)) {
				var accessToken = jwtService.generateToken(userDetails);
				authenticationService.revokeAllUserTokens(userResponse);
				authenticationService.saveUserToken(userResponse, accessToken);
				Date expirationDate = jwtService.extractExpiration(accessToken);
				authResponse = AuthenticationResponse.builder().accessToken(accessToken).refreshToken(token)
						.expiresAt(expirationDate).build();
			}
		}
		return ResponseEntity.ok(authResponse);
	}

	@Operation(summary = SWG_AUTH_LOGOUT_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_AUTH_LOGOUT_MESSAGE, content = {
					@Content(schema = @Schema(implementation = GenericResponse.class)) }),
			@ApiResponse(responseCode = "400", description = SWG_AUTH_LOGIN_ERROR, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping("/auth/logout")
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		logoutService.logout(request, response, authentication);
	}

	@Operation(summary = SWG_TOKEN_VALIDATION_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_TOKEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "400", description = SWG_AUTH_LOGIN_ERROR, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping(value = "/auth/validateToken")
	public ResponseEntity<Object> validate(@Valid @RequestParam String token) {
		String userEmail = null;
		Map<String, String> result = new HashMap<>();
		try {
			userEmail = jwtService.extractUsername(token);
		} catch (IllegalArgumentException e) {
			log.error(JWT_ILLEGAL_ARGUMENT_MESSAGE, e);
			result.put(MESSAGE_KEY, JWT_ILLEGAL_ARGUMENT_MESSAGE);
		} catch (ExpiredJwtException e) {
			log.warn(JWT_EXPIRED_MESSAGE, e);
			result.put(MESSAGE_KEY, JWT_EXPIRED_MESSAGE);
		} catch (JwtException e) {
			log.error(JWT_SIGNATURE_MESSAGE);
			result.put(MESSAGE_KEY, JWT_SIGNATURE_MESSAGE);
		}

		if (userEmail != null) {
			result.put(MESSAGE_KEY, VALIDATE_TOKEN_SUCCESS_MESSAGE);
			return ResponseEntity.ok(result);
		}

		return ResponseEntity.badRequest().body(result);
	}

	@Operation(summary = SWG_USER_LOGGED_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_LOGGED_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))) })
	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Object> currentUser() throws ResourceNotFoundException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String email = authentication.getName();
		return ResponseEntity.ok(userService.findByEmail(email));
	}

	// @PreAuthorize("hasRole('Admin')")
	@GetMapping("/demo")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Object> currentLogin(Principal principal) {
		return ResponseEntity.ok(principal);
	}
}
