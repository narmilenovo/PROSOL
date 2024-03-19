package com.example.user_management.controller;

import static com.example.user_management.utils.Constants.FORBIDDEN_MESSAGE;
import static com.example.user_management.utils.Constants.INVALID_DATA_MESSAGE;
import static com.example.user_management.utils.Constants.NO_USER_FOUND_WITH_EMAIL_MESSAGE;
import static com.example.user_management.utils.Constants.PASSWORD_LINK_SENT_MESSAGE;
import static com.example.user_management.utils.Constants.PASSWORD_NOT_MATCH_MESSAGE;
import static com.example.user_management.utils.Constants.RESET_PASSWORD_SUCCESS_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ASSIGN_ROLE_USER_OPERATION;
import static com.example.user_management.utils.Constants.SWG_AUTH_BULK_OPERATION;
import static com.example.user_management.utils.Constants.SWG_AUTH_REGISTER_ERROR;
import static com.example.user_management.utils.Constants.SWG_AUTH_REGISTER_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_AUTH_REGISTER_OPERATION;
import static com.example.user_management.utils.Constants.SWG_REMOVE_ROLE_USER_OPERATION;
import static com.example.user_management.utils.Constants.SWG_RES_PWD_FORGOT_ERROR;
import static com.example.user_management.utils.Constants.SWG_RES_PWD_FORGOT_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_RES_PWD_FORGOT_OPERATION;
import static com.example.user_management.utils.Constants.SWG_RES_PWD_RESET_ERROR;
import static com.example.user_management.utils.Constants.SWG_RES_PWD_RESET_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_RES_PWD_RESET_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_ASSIGN_ROLE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_USER_BATCH_DELETE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_DELETE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_USER_DELETE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_ITEM_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_USER_ITEM_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_LIST_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_USER_LIST_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_REMOVE_ROLE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_USER_TAG_DESCRIPTION;
import static com.example.user_management.utils.Constants.SWG_USER_TAG_NAME;
import static com.example.user_management.utils.Constants.SWG_USER_UPDATE_BATCH_STATUS_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_UPDATE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_USER_UPDATE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_UPDATE_PWD_ERROR;
import static com.example.user_management.utils.Constants.SWG_USER_UPDATE_PWD_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_USER_UPDATE_PWD_OPERATION;
import static com.example.user_management.utils.Constants.SWG_USER_UPDATE_STATUS_OPERATION;
import static com.example.user_management.utils.Constants.TOKEN_EXPIRED_MESSAGE;
import static com.example.user_management.utils.Constants.UNAUTHORIZED_MESSAGE;

import java.net.URI;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.user_management.client.plant.DepartmentResponse;
import com.example.user_management.client.plant.PlantResponse;
import com.example.user_management.client.plant.PlantServiceClient;
import com.example.user_management.dto.request.ForgotPasswordRequest;
import com.example.user_management.dto.request.ResetPasswordRequest;
import com.example.user_management.dto.request.UpdatePasswordRequest;
import com.example.user_management.dto.request.UpdateUserRequest;
import com.example.user_management.dto.request.UserAccountRequest;
import com.example.user_management.dto.request.UserRequest;
import com.example.user_management.dto.request.UserRoleRequest;
import com.example.user_management.dto.response.BadRequestResponse;
import com.example.user_management.dto.response.InvalidDataResponse;
import com.example.user_management.dto.response.UserResponse;
import com.example.user_management.events.OnResetPasswordEvent;
import com.example.user_management.exceptions.PasswordNotMatchException;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.service.interfaces.UserAccountService;
import com.example.user_management.service.interfaces.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@Tag(name = SWG_USER_TAG_NAME, description = SWG_USER_TAG_DESCRIPTION)
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final UserAccountService userAccountService;
	private final ApplicationEventPublisher eventPublisher;
	private final PlantServiceClient plantServiceClient;

	@Operation(summary = SWG_AUTH_REGISTER_OPERATION, responses = {
			@ApiResponse(responseCode = "201", description = SWG_AUTH_REGISTER_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "400", description = SWG_AUTH_REGISTER_ERROR, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping("/saveUser")
	public ResponseEntity<Object> saveUser(@Valid @RequestBody UserRequest userRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveUser").toUriString());
		UserResponse user = userService.saveUser(userRequest);
		return ResponseEntity.created(uri).body(user);
	}

	@Operation(summary = SWG_AUTH_BULK_OPERATION, responses = {
			@ApiResponse(responseCode = "201", description = SWG_AUTH_REGISTER_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "400", description = SWG_AUTH_REGISTER_ERROR, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })

	@PostMapping("/saveAllUser")
	public ResponseEntity<Object> bulkSave(@RequestBody List<UserRequest> userRequests) {
		List<UserResponse> users = userService.saveAllUser(userRequests);
		return ResponseEntity.ok(users);
	}

	@Operation(summary = SWG_USER_ITEM_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_ITEM_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))) })
	@GetMapping("/getUserById/{id}")
	public ResponseEntity<Object> getUserById(@PathVariable Long id,
			@Pattern(regexp = "p|d|pd") @RequestParam(required = false) String show) throws ResourceNotFoundException {
		if (show == null) {
			return ResponseEntity.ok(userService.getUserById(id, show));
		}
		Object userResponse = switch (show) {
		case "p" -> userService.getUserPlantById(id, show);
		case "d" -> userService.getUserDepartmentById(id, show);
		case "pd" -> userService.getUserDepartmentPlantById(id, show);
		default -> userService.getUserById(id, show);
		};
		return ResponseEntity.status(HttpStatus.OK).body(userResponse);
	}

	@Operation(summary = SWG_USER_LIST_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_LIST_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))) })
	@GetMapping(value = "/getAllUsers")
	public ResponseEntity<Object> getAllUsers(@Pattern(regexp = "p|d|pd") @RequestParam(required = false) String show) {
		if (show == null) {
			return ResponseEntity.ok(userService.getAllUsers(show));
		}
		List<?> users = switch (show) {
		case "p" -> userService.getAllUserPlants(show);
		case "d" -> userService.getAllUserDepartment(show);
		case "pd" -> userService.getAllUserDepartmentPlants(show);
		default -> userService.getAllUsers(show);
		};
		return ResponseEntity.ok(users);
	}

	@GetMapping(value = "/getAllUsersByPlantId")
	public ResponseEntity<Object> getAllUsers(@Pattern(regexp = "p|d|pd") @RequestParam(required = false) String show,
			@RequestBody List<Long> plantIds) {
		if (show == null) {
			return ResponseEntity.ok(userService.getAllUsersByPlantId(show, plantIds));
		}
		List<?> users = switch (show) {
		case "p" -> userService.getAllUserPlantsByPlantId(show, plantIds);
		case "d" -> userService.getAllUserDepartmentByPlantId(show, plantIds);
		case "pd" -> userService.getAllUserDepartmentPlantsByPlantId(show, plantIds);
		default -> userService.getAllUsersByPlantId(show, plantIds);
		};
		return ResponseEntity.ok(users);
	}

	@Operation(summary = SWG_USER_UPDATE_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PutMapping("/updateById/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
			@Valid @RequestBody UpdateUserRequest updateUserRequest) throws ResourceNotFoundException {
		UserResponse user = userService.updateUser(id, updateUserRequest);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = SWG_USER_UPDATE_STATUS_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PatchMapping("/updateStatusById/{id}")
	public ResponseEntity<Object> updateUserStatusId(@PathVariable Long id) throws ResourceNotFoundException {
		UserResponse user = userService.updateStatusUsingId(id);
		return ResponseEntity.ok(user);
	}

	@Operation(summary = SWG_USER_UPDATE_BATCH_STATUS_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PatchMapping("/updateBulkStatusUsingId")
	public ResponseEntity<Object> updateBulkStatusUsingId(@RequestBody List<Long> ids)
			throws ResourceNotFoundException {
		List<UserResponse> users = userService.updateBulkStatusUsingId(ids);
		return ResponseEntity.ok(users);
	}

	@Operation(summary = SWG_USER_UPDATE_PWD_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_PWD_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "400", description = SWG_USER_UPDATE_PWD_ERROR, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PutMapping("/{id}/changePassword")
	public ResponseEntity<Object> updatePassword(@PathVariable Long id,
			@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest)
			throws ResourceNotFoundException, PasswordNotMatchException {
		UserResponse user = userService.updatePassword(id, updatePasswordRequest);

		if (user == null) {
			throw new PasswordNotMatchException(PASSWORD_NOT_MATCH_MESSAGE);
		}

		return ResponseEntity.ok(user);
	}

	@Operation(summary = SWG_RES_PWD_FORGOT_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_RES_PWD_FORGOT_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "400", description = SWG_RES_PWD_FORGOT_ERROR, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(schema = @Schema(implementation = InvalidDataResponse.class))) })
	@PostMapping("/forgotPassword")
	public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest,
			final HttpServletRequest request) throws ResourceNotFoundException {
		UserResponse user = userService.findByEmail(forgotPasswordRequest.getEmail());
		if (user != null) {
			eventPublisher.publishEvent(new OnResetPasswordEvent(user, applicationUrl(request)));
			return ResponseEntity.ok(PASSWORD_LINK_SENT_MESSAGE);
		}
		return ResponseEntity.badRequest().body(NO_USER_FOUND_WITH_EMAIL_MESSAGE);
	}

	private String applicationUrl(HttpServletRequest request) {
//		return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
		return "http://localhost:3000";
	}

	@Operation(summary = SWG_RES_PWD_RESET_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_RES_PWD_RESET_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
			@ApiResponse(responseCode = "400", description = SWG_RES_PWD_RESET_ERROR, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(schema = @Schema(implementation = InvalidDataResponse.class))) })
	@PostMapping("/resetPassword")
	public ResponseEntity<Object> resetPassword(@RequestParam("token") String token,
			@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) throws ResourceNotFoundException {
		UserAccountRequest userAccount = userAccountService.findByToken(token);
		if (userAccount.isExpired()) {
			userAccountService.deleteById(userAccount.getId());
			return ResponseEntity.badRequest().body(TOKEN_EXPIRED_MESSAGE);
		}
		userService.updatePassword(userAccount.getUser().getId(), resetPasswordRequest.getPassword());
		userAccountService.deleteById(userAccount.getId());
		return ResponseEntity.ok().body(RESET_PASSWORD_SUCCESS_MESSAGE);
	}

	@Operation(summary = SWG_USER_DELETE_OPERATION, responses = {
			@ApiResponse(responseCode = "204", description = SWG_USER_DELETE_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))) })
	@DeleteMapping("/deleteUser/{id}")
	public ResponseEntity<Object> delete(@PathVariable Long id) throws ResourceNotFoundException {
		userService.deleteUserId(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = SWG_USER_BATCH_DELETE_OPERATION, responses = {
			@ApiResponse(responseCode = "204", description = SWG_USER_DELETE_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))) })
	@DeleteMapping("/deleteBatchUser")
	public ResponseEntity<Object> deleteBatch(@RequestBody List<Long> ids) throws ResourceNotFoundException {
		userService.deleteBatch(ids);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = SWG_REMOVE_ROLE_USER_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_REMOVE_ROLE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@DeleteMapping("/removeRolesFromUser/{id}")
	public ResponseEntity<Object> removeRolesFromUser(@PathVariable Long id,
			@Valid @RequestBody UserRoleRequest userRoleRequest) {
		try {
			UserResponse userResponse = userService.removeRolesFromUser(id, userRoleRequest);
			return ResponseEntity.ok().body(userResponse);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(summary = SWG_ASSIGN_ROLE_USER_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_USER_ASSIGN_ROLE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = UserResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })

	@PutMapping("/addRolesToUser/{id}")
	public ResponseEntity<Object> addRolesToUser(@PathVariable Long id,
			@Valid @RequestBody UserRoleRequest userRoleRequest) {
		try {
			UserResponse userResponse = userService.addRolesToUser(id, userRoleRequest);
			return ResponseEntity.ok().body(userResponse);
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/getDepartmentById/{id}")
	public DepartmentResponse demo(@RequestParam Long id) {
		return plantServiceClient.getDepartmentById(id);
	}

	@GetMapping("/getPlantById/{plantId}")
	PlantResponse getPlantById(@PathVariable Long plantId) {
		return plantServiceClient.getPlantById(plantId);
	}

}
