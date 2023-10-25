package com.example.user_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "UpdateUserRolesParam", description = "Parameters required to update user roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleRequest {
    @Schema(description = "Array of role to add or remove")
    @NotEmpty(message = "The field must have at least one item")
    private Long[] roles;
}