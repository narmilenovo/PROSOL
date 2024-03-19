package com.example.batchservice.request;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
	@Email(message = "Email address is not valid")
	@Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
	@NotBlank(message = "The email address is required")
	private String email;

	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.")
	private String password;

	@NotBlank(message = "This field is required")
	private String confirmPassword;

	@NotBlank(message = "The first name is required")
	private String firstName;

	@NotBlank(message = "The last name is required")
	private String lastName;

	@Pattern(regexp = "^[1-9]\\d{9}$", message = "Invalid phone number")
	private String phone;

	private String business;

	private Long departmentId;

	private List<Long> plantId;

	private Boolean status = true;

	private Long[] roles;
}
