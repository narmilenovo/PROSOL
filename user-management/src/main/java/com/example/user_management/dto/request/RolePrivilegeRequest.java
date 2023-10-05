package com.example.user_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "UpdateRolePermissionParam", description = "Parameters required to update role permissions")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RolePrivilegeRequest {
    @Schema(description = "Array of privileges to give or remove to a role")
    @NotEmpty(message = "The field must have at least one item")
    private String[] privileges;
}