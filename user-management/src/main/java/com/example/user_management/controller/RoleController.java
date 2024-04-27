package com.example.user_management.controller;

import static com.example.user_management.utils.Constants.FORBIDDEN_MESSAGE;
import static com.example.user_management.utils.Constants.INVALID_DATA_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ASSIGN_PRIVILEGES_ROLE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_REMOVE_PRIVILEGES_ROLE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_ASSIGN_PRIVILEGES_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ROLE_CREATE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ROLE_CREATE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_DELETE_BATCH_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_DELETE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ROLE_DELETE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_ITEM_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ROLE_ITEM_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_LIST_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ROLE_LIST_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_REMOVE_PRIVILEGES_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ROLE_TAG_DESCRIPTION;
import static com.example.user_management.utils.Constants.SWG_ROLE_TAG_NAME;
import static com.example.user_management.utils.Constants.SWG_ROLE_TRUE_LIST_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_UPDATE_BATCH_STATUS_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_UPDATE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_ROLE_UPDATE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_ROLE_UPDATE_STATUS_OPERATION;
import static com.example.user_management.utils.Constants.UNAUTHORIZED_MESSAGE;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
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

import com.example.user_management.dto.request.RolePrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.BadRequestResponse;
import com.example.user_management.dto.response.InvalidDataResponse;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.service.interfaces.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = SWG_ROLE_TAG_NAME, description = SWG_ROLE_TAG_DESCRIPTION)
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class RoleController {
	private final RoleService roleService;

	@Operation(summary = SWG_ROLE_CREATE_OPERATION, responses = {
			@ApiResponse(responseCode = "201", description = SWG_ROLE_CREATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping("/saveRole")
	public ResponseEntity<Object> saveRole(@Valid @RequestBody RoleRequest roleRequest)
			throws ResourceFoundException, ResourceNotFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/saveRole").toUriString());
		RoleResponse savedRole = roleService.saveRole(roleRequest);
		return ResponseEntity.created(uri).body(savedRole);
	}

	@Operation(summary = SWG_ROLE_ITEM_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_ROLE_ITEM_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }) })
	@GetMapping("/getRoleById/{id}")
	public ResponseEntity<Object> getRoleById(@PathVariable @NonNull Long id, @RequestParam Boolean show)
			throws ResourceNotFoundException {
		Object role;
		if (Boolean.TRUE.equals(show)) {
			role = roleService.getRolePlantById(id);
		} else {
			role = roleService.getRoleById(id);
		}
		return ResponseEntity.status(HttpStatus.OK).body(role);
	}

	@Operation(summary = SWG_ROLE_LIST_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_ROLE_LIST_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }) })
	@GetMapping("/getAllRoles")
	public ResponseEntity<Object> getAllRoles(@RequestParam Boolean show) {
		List<?> roles;
		if (Boolean.TRUE.equals(show)) {
			roles = roleService.getAllRolesPlant();
		} else {
			roles = roleService.getAllRoles();
		}
		return ResponseEntity.ok(roles);
	}

	@Operation(summary = SWG_ROLE_TRUE_LIST_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_ROLE_LIST_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }) })

	@GetMapping("/getAllRolesTrue")
	public ResponseEntity<Object> listRoleStatusTrue(@RequestParam Boolean show) {
		List<?> responseList;
		if (Boolean.TRUE.equals(show)) {
			responseList = roleService.findAllRolesPlantStatusTrue();
		} else {

			responseList = roleService.findAllStatusTrue();
		}
		return ResponseEntity.ok(responseList);
	}

	@GetMapping("/getAllRolesByPlantId/{plantId}")
	public ResponseEntity<Object> listRolesByPlantId(@RequestParam Boolean show, @PathVariable Long plantId) {
		List<?> responseList;
		if (Boolean.TRUE.equals(show)) {
			responseList = roleService.findAllByPlantId(plantId);
		} else {

			responseList = roleService.findAllRolesPlantByPlantId(plantId);
		}
		return ResponseEntity.ok(responseList);
	}

	@Operation(summary = SWG_ROLE_UPDATE_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_ROLE_UPDATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PutMapping("/updateRole/{id}")
	public ResponseEntity<Object> updateRole(@PathVariable @NonNull Long id,
			@Valid @RequestBody RoleRequest updateRoleRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		RoleResponse role = roleService.updateRole(id, updateRoleRequest);
		return ResponseEntity.ok(role);
	}

	@Operation(summary = SWG_ROLE_UPDATE_STATUS_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_ROLE_UPDATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })

	@PatchMapping("/updateRoleStatusById/{id}")
	public ResponseEntity<Object> updateRoleStatusId(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		RoleResponse response = roleService.updateStatusUsingRoleId(id);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = SWG_ROLE_UPDATE_BATCH_STATUS_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_ROLE_UPDATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PatchMapping("/updateBulkStatusRoleId")
	public ResponseEntity<Object> updateBulkStatusRoleId(@RequestBody @NonNull List<Long> id)
			throws ResourceNotFoundException {
		List<RoleResponse> responseList = roleService.updateBulkStatusRoleId(id);
		return ResponseEntity.ok(responseList);
	}

	@Operation(summary = SWG_ROLE_DELETE_OPERATION, responses = {
			@ApiResponse(responseCode = "204", description = SWG_ROLE_DELETE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@DeleteMapping("/deleteRole/{id}")
	public ResponseEntity<Object> deleteRole(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		roleService.deleteRoleId(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = SWG_ROLE_DELETE_BATCH_OPERATION, responses = {
			@ApiResponse(responseCode = "204", description = SWG_ROLE_DELETE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@DeleteMapping("/deleteBatchRole")
	public ResponseEntity<Object> deleteBatchRole(@RequestBody @NonNull List<Long> id)
			throws ResourceNotFoundException {
		roleService.deleteBatchRole(id);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = SWG_ASSIGN_PRIVILEGES_ROLE_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_ROLE_ASSIGN_PRIVILEGES_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })

	@PatchMapping("/assignPrivilegesToRole/{roleId}")
	public ResponseEntity<Object> assignPrivilegesToRole(@PathVariable @NonNull Long roleId,
			@Valid @RequestBody RolePrivilegeRequest rolePrivilegeRequest) throws ResourceNotFoundException {
		RoleResponse roleResponse = roleService.assignPrivilegesToRole(roleId, rolePrivilegeRequest);
		return ResponseEntity.ok().body(roleResponse);
	}

	@Operation(summary = SWG_REMOVE_PRIVILEGES_ROLE_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_ROLE_REMOVE_PRIVILEGES_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@DeleteMapping("/unassignPrivilegesFromRole/{id}")
	public ResponseEntity<Object> unassignPrivilegesFromRole(@PathVariable @NonNull Long id,
			@Valid @RequestBody RolePrivilegeRequest rolePrivilegeRequest) throws ResourceNotFoundException {
		roleService.unassignPrivilegesFromRole(id, rolePrivilegeRequest);
		return ResponseEntity.noContent().build();
	}

}
