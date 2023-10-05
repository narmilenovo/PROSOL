package com.example.user_management.controller;

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

@Tag(name = SWG_PRIVILEGE_TAG_NAME, description = SWG_PRIVILEGE_TAG_DESCRIPTION)
@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
//@RequestMapping("/user")
public class PrivilegeController {
    private final PrivilegeService privilegeService;

    @Operation(summary = SWG_PRIVILEGE_CREATE_OPERATION, responses = {
            @ApiResponse(responseCode = "201", description = SWG_PRIVILEGE_CREATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PrivilegeResponse.class))
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
    @PostMapping("/savePrivilege")
    public ResponseEntity<Object> savePrivilege(@Valid @RequestBody PrivilegeRequest privilegeRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/savePrivilege").toUriString());
        PrivilegeResponse savedPrivilege = privilegeService.savePrivilege(privilegeRequest);
        return ResponseEntity.created(uri).body(savedPrivilege);
    }

    @Operation(summary = SWG_PRIVILEGE_ITEM_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_PRIVILEGE_ITEM_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "404", description = NO_PRIVILEGE_FOUND_WITH_ID_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            })
    })
    @GetMapping("/getPrivilegeById/{id}")
    public ResponseEntity<Object> getPrivilegeById(@PathVariable Long id) throws ResourceNotFoundException {
        PrivilegeResponse foundPrivilege = privilegeService.getPrivilegeById(id);
        return ResponseEntity.ok(foundPrivilege);
    }

    @Operation(summary = SWG_PRIVILEGE_LIST_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_PRIVILEGE_LIST_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = RoleResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            })
    })
    @GetMapping("/getAllPrivileges")
    public ResponseEntity<Object> getAllPrivileges() {
        List<PrivilegeResponse> privileges = privilegeService.getAllPrivileges();
        return ResponseEntity.ok(privileges);
    }

    @Operation(summary = SWG_PRIVILEGE_UPDATE_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_PRIVILEGE_UPDATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = PrivilegeResponse.class))
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

    @PutMapping("/updatePrivilege/{id}")
    public ResponseEntity<Object> updatePrivilege(@PathVariable Long id, @Valid @RequestBody PrivilegeRequest updatePrivilegeRequest) throws ResourceNotFoundException, ResourceFoundException {
        PrivilegeResponse updatePrivilege = privilegeService.updatePrivilege(id, updatePrivilegeRequest);
        return ResponseEntity.ok().body(updatePrivilege);
    }

    @Operation(summary = SWG_PRIVILEGE_DELETE_OPERATION, responses = {
            @ApiResponse(responseCode = "204", description = SWG_PRIVILEGE_DELETE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            })
    })
    @DeleteMapping("/deletePrivilege/{id}")
    public ResponseEntity<String> deletePrivilege(@PathVariable Long id) throws ResourceNotFoundException {
        privilegeService.deletePrivilege(id);
        return ResponseEntity.noContent().build();
    }
}
