package com.example.user_management.controller;

import com.example.user_management.dto.request.*;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.example.user_management.utils.Constants.*;

@Tag(name = SWG_USER_TAG_NAME, description = SWG_USER_TAG_DESCRIPTION)
@RestController
@RequiredArgsConstructor
//@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class UserController {

    private final UserService userService;
    private final UserAccountService userAccountService;
    private final ApplicationEventPublisher eventPublisher;

    @Operation(summary = SWG_AUTH_REGISTER_OPERATION, responses = {
            @ApiResponse(responseCode = "201", description = SWG_AUTH_REGISTER_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = SWG_AUTH_REGISTER_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })

    @PostMapping("/saveUser")
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserRequest userRequest) throws ResourceFoundException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/saveUser").toUriString());
        UserResponse user = userService.saveUser(userRequest);
        return ResponseEntity.created(uri).body(user);
    }

    @Operation(summary = SWG_AUTH_BULK_OPERATION, responses = {
            @ApiResponse(responseCode = "201", description = SWG_AUTH_REGISTER_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = SWG_AUTH_REGISTER_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })

    @PostMapping("/saveAllUser")
    public ResponseEntity<Object> bulkSave(@Valid @RequestBody List<UserRequest> userRequests) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/bulkSave").toUriString());
        List<UserResponse> users = userService.saveAllUser(userRequests);
        return ResponseEntity.created(uri).body(users);
    }

    @Operation(summary = SWG_USER_LIST_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_LIST_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class)))
    })
    @GetMapping(value = "/getAllUsers")
    public ResponseEntity<Object> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = SWG_USER_ITEM_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_ITEM_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class)))
    })
    @GetMapping("/getUserById/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) throws ResourceNotFoundException {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @Operation(summary = SWG_USER_DELETE_OPERATION, responses = {
            @ApiResponse(responseCode = "204", description = SWG_USER_DELETE_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class)))
    })
    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) throws ResourceNotFoundException {
        userService.deleteUserId(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = SWG_USER_BATCH_DELETE_OPERATION, responses = {
            @ApiResponse(responseCode = "204", description = SWG_USER_DELETE_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class)))
    })
    @DeleteMapping("/deleteBatchUser/{id}")
    public ResponseEntity<Object> deleteBatch(@PathVariable List<Long> id) {
        userService.deleteBatch(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = SWG_USER_UPDATE_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
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
    @PutMapping("/updateById/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateUserRequest) throws ResourceNotFoundException {
        UserResponse user = userService.updateUser(id, updateUserRequest);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = SWG_USER_UPDATE_STATUS_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
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
    @PatchMapping("/updateStatusById/{id}")
    public ResponseEntity<Object> updateUserStatusId(@PathVariable Long id) throws ResourceNotFoundException {
        UserResponse user = userService.updateStatusUsingId(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = SWG_USER_UPDATE_BATCH_STATUS_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
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
    @PatchMapping("/updateBulkStatusUsingId/{id}")
    public ResponseEntity<Object> updateBulkStatusUsingId(@PathVariable List<Long> id) {
        List<UserResponse> users = userService.updateBulkStatusUsingId(id);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = SWG_USER_UPDATE_PWD_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_PWD_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = SWG_USER_UPDATE_PWD_ERROR, content = {
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
    @PutMapping("/{id}/changePassword")
    public ResponseEntity<Object> updatePassword(@PathVariable Long id, @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest) throws ResourceNotFoundException, PasswordNotMatchException {
        UserResponse user = userService.updatePassword(id, updatePasswordRequest);

        if (user == null) {
            throw new PasswordNotMatchException(PASSWORD_NOT_MATCH_MESSAGE);
        }

        return ResponseEntity.ok(user);
    }

    @Operation(summary = SWG_RES_PWD_FORGOT_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_RES_PWD_FORGOT_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = SWG_RES_PWD_FORGOT_ERROR, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(schema = @Schema(implementation = InvalidDataResponse.class)))
    })
    @PostMapping("/forgotPassword")
    public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest, final HttpServletRequest request) throws ResourceNotFoundException {
        UserResponse user = userService.findByEmail(forgotPasswordRequest.getEmail());
        if (user != null) {
            eventPublisher.publishEvent(new OnResetPasswordEvent(user, applicationUrl(request)));
            return ResponseEntity.ok(PASSWORD_LINK_SENT_MESSAGE);
        }
        return ResponseEntity.badRequest().body(NO_USER_FOUND_WITH_EMAIL_MESSAGE);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() +
                ":" + request.getServerPort() +
                request.getContextPath();
    }

    @Operation(summary = SWG_RES_PWD_RESET_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_RES_PWD_RESET_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "400", description = SWG_RES_PWD_RESET_ERROR, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(schema = @Schema(implementation = InvalidDataResponse.class)))
    })
    @PostMapping("/resetPassword")
    public ResponseEntity<Object> resetPassword(@RequestParam("token") String token, @Valid @RequestBody ResetPasswordRequest resetPasswordRequest) throws ResourceNotFoundException {
        UserAccountRequest userAccount = userAccountService.findByToken(token);
        if (userAccount.isExpired()) {
            userAccountService.delete(userAccount.getId());
            return ResponseEntity.badRequest().body(TOKEN_EXPIRED_MESSAGE);
        }
        userService.updatePassword(userAccount.getUser().getId(), resetPasswordRequest.getPassword());
        userAccountService.delete(userAccount.getId());
        return ResponseEntity.badRequest().body(RESET_PASSWORD_SUCCESS_MESSAGE);
    }

    @Operation(summary = SWG_REMOVE_ROLE_USER_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_REMOVE_ROLE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
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
    @DeleteMapping("/removeRolesFromUser/{id}")
    public ResponseEntity<Object> removeRolesFromUser(@PathVariable Long id, @Valid @RequestBody UserRoleRequest userRoleRequest) {
        try {
            UserResponse userResponse = userService.removeRolesFromUser(id, userRoleRequest);
            return ResponseEntity.ok().body(userResponse);
        } catch (ResourceNotFoundException ex) {
            // Handle the exception appropriately, e.g., return a 404 response
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = SWG_ASSIGN_ROLE_USER_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_ASSIGN_ROLE_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
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

    @PutMapping("/addRolesToUser/{id}")
    public ResponseEntity<Object> addRolesToUser(@PathVariable Long id, @Valid @RequestBody UserRoleRequest userRoleRequest) {
        try {
            UserResponse userResponse = userService.addRolesToUser(id, userRoleRequest);
            return ResponseEntity.ok().body(userResponse);
        } catch (ResourceNotFoundException ex) {
            // Handle the exception appropriately, e.g., return a 404 response
            return ResponseEntity.notFound().build();
        }
    }


}
