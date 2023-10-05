package com.example.user_management.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Schema(name = "UpdatePasswordParam", description = "Parameters required to update the password")
@Accessors(chain = true)
@Setter
@Getter
public class UpdatePasswordRequest {
    @Schema(description = "Current user password", minLength = 6)
    @NotBlank(message = "This field is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.")
    private String currentPassword;

    @Schema(description = "New user password", minLength = 6)
    @NotBlank(message = "This field is required")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$", message = "Password must be at least 6 characters long and contain at least one uppercase letter, one lowercase letter, and one digit.")
    private String newPassword;
}
