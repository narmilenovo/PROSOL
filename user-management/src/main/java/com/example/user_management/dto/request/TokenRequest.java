package com.example.user_management.dto.request;

import com.example.user_management.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenRequest {

    private String tokenValue;

    private TokenType tokenType = TokenType.BEARER;

    private boolean revoked;

    private boolean expired;

    private UserResponse user;

}
