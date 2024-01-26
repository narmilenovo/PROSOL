package com.example.user_management.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "Parameters required to create or update user")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequest {
	private String firstName;

	private String lastName;

	@Pattern(regexp = "^[1-9]\\d{9}$", message = "Invalid phone number")
	private String phone;

	private String business;

	private Long departmentId;

	private List<Long> plantId;

	private Boolean status = true;

	private Long[] roles;
}
