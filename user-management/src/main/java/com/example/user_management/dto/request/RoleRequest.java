package com.example.user_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
	@Schema(description = "Name of the role", example = "A")
	@NotBlank(message = "The name is required")
	private String name;

	@Schema(description = "Description of the role")
	private String description;

	@Schema(description = "Role based plant Name")
	private Long plantId;

	@Schema(description = "Defines Role status")
	private Boolean status = true;

	@Schema(description = "Set Privileges to Role")
	private Long[] privileges;
}
