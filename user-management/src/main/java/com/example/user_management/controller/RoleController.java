package com.example.user_management.controller;

import com.example.user_management.dto.request.RolePrivilegeRequest;
import com.example.user_management.dto.request.RoleRequest;
import com.example.user_management.dto.response.BadRequestResponse;
import com.example.user_management.dto.response.InvalidDataResponse;
import com.example.user_management.dto.response.RoleResponse;
import com.example.user_management.exceptions.ResourceFoundException;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.service.interfaces.PrivilegeService;
import com.example.user_management.service.interfaces.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.example.user_management.utils.Constants.*;

@Tag(name = SWG_ROLE_TAG_NAME, description = SWG_ROLE_TAG_DESCRIPTION)
@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
//@RequestMapping("/user")
public class RoleController {
    private final RoleService roleService;
    private final PrivilegeService privilegeService;


    @Operation(summary = SWG_ROLE_CREATE_OPERATION, responses = {
            @ApiResponse(responseCode = "201", description = SWG_ROLE_CREATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @PostMapping("/saveRole")
    public ResponseEntity<Object> saveRole(@Valid @RequestBody RoleRequest roleRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/saveRole").toUriString());
        RoleResponse savedRole = roleService.saveRole(roleRequest);
        return ResponseEntity.created(uri).body(savedRole);
    }

    @Operation(summary = SWG_ROLE_LIST_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_LIST_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            })
    })
    @GetMapping("/getAllRoles")
    public ResponseEntity<Object> getAllRoles() {
        List<RoleResponse> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @Operation(summary = SWG_ROLE_ITEM_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_ITEM_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            })
    })
    @GetMapping("/getRoleById/{id}")
    public ResponseEntity<Object> getRoleById(@PathVariable Long id) throws ResourceNotFoundException {
        RoleResponse role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = SWG_ROLE_TRUE_LIST_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_LIST_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            })
    })

    @GetMapping("/getAllRolesTrue")
    public ResponseEntity<Object> listRoleStatusTrue() {
        List<RoleResponse> responseList = roleService.findAllStatusTrue();
        return ResponseEntity.ok(responseList);
    }

/*    @GetMapping("/getAllRoleUserEmail")
    public ResponseEntity<Object> getAllRoleUserEmail() {
        List<RoleUserDto> roleUserDtos = roleService.getAllRoleUserEmail();
        return ResponseEntity.ok(roleUserDtos);
    }*/

    @Operation(summary = SWG_ROLE_UPDATE_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_UPDATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @PutMapping("/updateRole/{id}")
    public ResponseEntity<Object> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest updateRoleRequest) throws ResourceNotFoundException, ResourceFoundException {
        RoleResponse role = roleService.updateRole(id, updateRoleRequest);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = SWG_ROLE_UPDATE_STATUS_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_UPDATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })

    @PatchMapping("/updateRoleStatusById/{id}")
    public ResponseEntity<Object> updateRoleStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        RoleResponse response = roleService.updateStatusUsingRoleId(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = SWG_ROLE_UPDATE_BATCH_STATUS_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_UPDATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @PatchMapping("/updateBulkStatusRoleId/{id}")
    public ResponseEntity<Object> updateBulkStatusRoleId(@PathVariable List<Long> id) {
        List<RoleResponse> responseList = roleService.updateBulkStatusRoleId(id);
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = SWG_ROLE_DELETE_OPERATION, responses = {
            @ApiResponse(responseCode = "204", description = SWG_ROLE_DELETE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @DeleteMapping("/deleteRole/{id}")
    public ResponseEntity<Object> deleteRole(@PathVariable Long id) throws ResourceNotFoundException {
        roleService.deleteRoleId(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = SWG_ROLE_DELETE_BATCH_OPERATION, responses = {
            @ApiResponse(responseCode = "204", description = SWG_ROLE_DELETE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @DeleteMapping("/deleteBatchRole/{id}")
    public ResponseEntity<Object> deleteBatchRole(@PathVariable List<Long> id) {
        roleService.deleteBatchRole(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = SWG_REMOVE_PRIVILEGES_ROLE_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_REMOVE_PRIVILEGES_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @DeleteMapping("/removePrivilegesFromRole/{id}")
    public ResponseEntity<Object> removePrivilegesFromRole(@PathVariable Long id, @Valid @RequestBody RolePrivilegeRequest rolePrivilegeRequest) throws ResourceNotFoundException {
        RoleResponse roleResponse = roleService.removePrivilegesFromRole(id, rolePrivilegeRequest);
        return ResponseEntity.ok().body(roleResponse);
    }

    @Operation(summary = SWG_ASSIGN_PRIVILEGES_ROLE_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_ASSIGN_PRIVILEGES_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })

    @PutMapping("/addPrivilegesToRole/{id}")
    public ResponseEntity<Object> addPrivilegesToRole(@PathVariable Long id, @Valid @RequestBody RolePrivilegeRequest rolePrivilegeRequest)
            throws ResourceNotFoundException {
        RoleResponse roleResponse = roleService.addPrivilegesToRole(id, rolePrivilegeRequest);
        return ResponseEntity.ok().body(roleResponse);
    }
}