package com.example.user_management.dto.request;

import com.example.user_management.constraints.Exists;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(name = "ForgotPasswordParam", description = "Parameters required to request a reset link")
@Exists.List({
        @Exists(property = "email", repository = "UserRepository", message = "This email doesn't exists in the db!")
})
@Accessors(chain = true)
@Setter
@Getter
public class ForgotPasswordRequest {
    @Schema(description = "The email address to send the link to", example = "trialforall2022@gmail.com")
    @Email(message = "Email address is not valid")
    @NotBlank(message = "The email address is required")
    private String email;
}
