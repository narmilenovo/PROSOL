package com.example.user_management.controller;

import static com.example.user_management.utils.Constants.FORBIDDEN_MESSAGE;
import static com.example.user_management.utils.Constants.INVALID_DATA_MESSAGE;
import static com.example.user_management.utils.Constants.NO_PRIVILEGE_FOUND_WITH_ID_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_CREATE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_CREATE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_DELETE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_DELETE_OPERATION;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_ITEM_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_ITEM_OPERATION;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_LIST_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_LIST_OPERATION;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_TAG_DESCRIPTION;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_TAG_NAME;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_UPDATE_MESSAGE;
import static com.example.user_management.utils.Constants.SWG_PRIVILEGE_UPDATE_OPERATION;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.user_management.dto.request.PrivilegeRequest;
import com.example.user_management.dto.response.BadRequestResponse;
import com.example.user_management.dto.response.InvalidDataResponse;
import com.example.user_management.dto.response.PrivilegeResponse;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.service.interfaces.PrivilegeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = SWG_PRIVILEGE_TAG_NAME, description = SWG_PRIVILEGE_TAG_DESCRIPTION)
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class PrivilegeController {
	private final PrivilegeService privilegeService;

	@Operation(summary = SWG_PRIVILEGE_CREATE_OPERATION, responses = {
			@ApiResponse(responseCode = "201", description = SWG_PRIVILEGE_CREATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = PrivilegeResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })
	@PostMapping("/savePrivilege")
	public ResponseEntity<Object> savePrivilege(@Valid @RequestBody PrivilegeRequest privilegeRequest)
			throws ResourceFoundException {
		URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/savePrivilege").toUriString());
		PrivilegeResponse savedPrivilege = privilegeService.savePrivilege(privilegeRequest);
		return ResponseEntity.created(uri).body(savedPrivilege);
	}

	@Operation(summary = SWG_PRIVILEGE_ITEM_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_PRIVILEGE_ITEM_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "404", description = NO_PRIVILEGE_FOUND_WITH_ID_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }) })
	@GetMapping("/getPrivilegeById/{id}")
	public ResponseEntity<Object> getPrivilegeById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		PrivilegeResponse foundPrivilege = privilegeService.getPrivilegeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(foundPrivilege);
	}

	@Operation(summary = SWG_PRIVILEGE_LIST_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_PRIVILEGE_LIST_MESSAGE, content = {
					@Content(schema = @Schema(implementation = RoleResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }) })
	@GetMapping("/getAllPrivileges")
	public ResponseEntity<Object> getAllPrivileges() {
		List<PrivilegeResponse> privileges = privilegeService.getAllPrivileges();
		return ResponseEntity.ok(privileges);
	}

	@Operation(summary = SWG_PRIVILEGE_UPDATE_OPERATION, responses = {
			@ApiResponse(responseCode = "200", description = SWG_PRIVILEGE_UPDATE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = PrivilegeResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
					@Content(schema = @Schema(implementation = InvalidDataResponse.class)) }) })

	@PutMapping("/updatePrivilege/{id}")
	public ResponseEntity<Object> updatePrivilege(@PathVariable @NonNull Long id,
			@Valid @RequestBody PrivilegeRequest updatePrivilegeRequest)
			throws ResourceNotFoundException, ResourceFoundException {
		PrivilegeResponse updatePrivilege = privilegeService.updatePrivilege(id, updatePrivilegeRequest);
		return ResponseEntity.ok().body(updatePrivilege);
	}

	@PatchMapping("/updatePrivilegeStatusById/{id}")
	public ResponseEntity<Object> updatePrivilegeStatusById(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		PrivilegeResponse response = privilegeService.updateStatusUsingPrivilegeById(id);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@PatchMapping("/updateBulkStatusPrivilegeId")
	public ResponseEntity<Object> updateBulkStatusPrivilegeById(@RequestBody @NonNull List<Long> ids)
			throws ResourceNotFoundException {
		List<PrivilegeResponse> responseList = privilegeService.updateBulkStatusPrivilegeById(ids);
		return ResponseEntity.ok(responseList);
	}

	@Operation(summary = SWG_PRIVILEGE_DELETE_OPERATION, responses = {
			@ApiResponse(responseCode = "204", description = SWG_PRIVILEGE_DELETE_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }),
			@ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
					@Content(schema = @Schema(implementation = BadRequestResponse.class)) }) })
	@DeleteMapping("/deletePrivilege/{id}")
	public ResponseEntity<String> deletePrivilege(@PathVariable @NonNull Long id) throws ResourceNotFoundException {
		privilegeService.deletePrivilege(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/deleteBatchPrivilege")
	public ResponseEntity<Object> deleteBatchPrivilege(@RequestBody @NonNull List<Long> ids) throws ResourceNotFoundException {
		privilegeService.deleteBatchPrivilege(ids);
		return ResponseEntity.noContent().build();
	}

}
