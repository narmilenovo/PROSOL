package com.example.user_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    @Schema(description = "User email address", example = "trialforall2022@gmail.com")
    @Email(message = "Email address is not valid")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
    @NotBlank(message = "The email address is required")
    private String email;

    @Schema(description = "User's password (must be at least 6 characters)", minLength = 6, example = "Zz12345")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.")
    private String password;

    @Schema(description = "Password confirmation", example = "Zz12345")
    @NotBlank(message = "This field is required")
    private String confirmPassword;


    @Schema(description = "User first name")
    @NotBlank(message = "The first name is required")
    private String firstName;

    @Schema(description = "User last name")
    @NotBlank(message = "The last name is required")
    private String lastName;

    @Pattern(regexp = "^\\+91[1-9]\\d{9}$", message = "Invalid phone number")
    private String phone;

    private String business;

    private Long departmentId;

    private List<Long> plantId;

    @Schema(description = "Indicates if the user will be enabled or not")
    private Boolean status = true;

    private Long[] roles;
}
