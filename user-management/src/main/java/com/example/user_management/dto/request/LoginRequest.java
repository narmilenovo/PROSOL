package com.example.user_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "LoginUserParam", description = "Parameters required to login user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

	@Schema(description = "User email address", example = "trialforall2022@gmail.com")
	@Email(message = "Email address is not valid")
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
	@NotBlank(message = "The email address is required")
	private String email;

	@Schema(description = "User password (Min character: 6)", minLength = 6, example = "Zz12345")
	@Size(min = 6, message = "Must be at least 6 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.")
	private String password;
}
