package com.example.user_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "addRolesToUserParam", description = "Parameters required to update role users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleUserRequest {
	@Schema(description = "Array of user to add or remove")
	@NotEmpty(message = "The field must have at least one item")
	private Long[] users;
}
