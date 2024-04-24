package com.example.user_management.dto.request;

import com.example.user_management.constraints.FieldMatch;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "ResetPasswordParam", description = "Parameters required to reset password")
@FieldMatch.List({
		@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match") })
//@Accessors(chain = true)
@Setter
@Getter
public class ResetPasswordRequest {
	@Schema(description = "New value of the password", example = "Zz54321")
	@Size(min = 6, message = "Must be at least 6 characters")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.")
	private String password;

	@Schema(description = "Confirmation of the new value of the password", example = "Zz54321")
	@NotBlank(message = "This field is required")
	private String confirmPassword;
}
