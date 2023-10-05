package com.example.user_management.controller;

import com.example.user_management.dto.request.LoginRequest;
import com.example.user_management.dto.response.*;
import com.example.user_management.entity.User;
import com.example.user_management.exceptions.ResourceNotFoundException;
import com.example.user_management.security.CustomUserDetails;
import com.example.user_management.service.JwtService;
import com.example.user_management.service.LogoutService;
import com.example.user_management.service.interfaces.AuthenticationService;
import com.example.user_management.service.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.user_management.utils.Constants.*;

@Tag(name = SWG_AUTH_TAG_NAME, description = SWG_AUTH_TAG_DESCRIPTION)
@RestController
@RequiredArgsConstructor
//@RequestMapping("/user")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;
    private final UserService userService;
    private final LogoutService logoutService;
    private final AuthenticationManager authenticationManager;
    private final RestTemplate restTemplate;


    @Operation(summary = SWG_AUTH_LOGIN_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_AUTH_LOGIN_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthenticationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = SWG_AUTH_LOGIN_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @PostMapping("/auth/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest) throws ResourceNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        UserResponse userResponse = userService.findByEmail(loginRequest.getEmail());
        Map<String, String> result = new HashMap<>();

        if (userResponse.getStatus().equals(false)) {
            result.put(DATA_KEY, ACCOUNT_DEACTIVATED_MESSAGE);

            return ResponseEntity.badRequest().body(result);
        }
        User user = modelMapper.map(userResponse, User.class);
        UserDetails userDetails = new CustomUserDetails(user);
        var jwtToken = jwtService.generateToken(userDetails);
        var refreshToken = jwtService.generateRefreshToken(userDetails);
        Date expirationDate = jwtService.extractExpiration(jwtToken);
        authenticationService.revokeAllUserTokens(userResponse);
        authenticationService.saveUserToken(userResponse, jwtToken);
        System.out.println("jwtToken:--------------------------------- " + jwtToken);
        return ResponseEntity.ok(new AuthenticationResponse(jwtToken, refreshToken, expirationDate));
    }

    @Operation(summary = SWG_REFRESH_TOKEN_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_TOKEN_REFRESH_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuthenticationResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = SWG_AUTH_LOGIN_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @PostMapping("/auth/refresh-token")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException, ResourceNotFoundException {
        authenticationService.refreshToken(request, response);
    }

    @Operation(summary = SWG_AUTH_LOGOUT_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_AUTH_LOGOUT_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = GenericResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = SWG_AUTH_LOGIN_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })
    @PostMapping("/auth/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutService.logout(request, response, authentication);
    }

    @Operation(summary = SWG_TOKEN_VALIDATION_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_TOKEN__MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = SWG_AUTH_LOGIN_ERROR, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = BadRequestResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = InvalidDataResponse.class))
            })
    })

    @PostMapping(value = "/auth/validateToken")
    public UserResponse valid(@Valid @RequestParam String token) {
        return authenticationService.validation(token);
    }

    @Operation(summary = SWG_USER_LOGGED_OPERATION, responses = {
            @ApiResponse(responseCode = "200", description = SWG_USER_LOGGED_MESSAGE, content = @Content(schema = @Schema(implementation = UserResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class)))
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<Object> currentUser() throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
//        Object principal = authentication.getPrincipal();
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        return ResponseEntity.ok(userService.findByEmail(email));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/demo")
    public ResponseEntity<Object> currentLogin(Principal principal) throws ResourceNotFoundException {
        return ResponseEntity.ok(principal);
    }
}
