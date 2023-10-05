package com.example.user_management.dto.request;

import com.example.user_management.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountRequest {
    private Long id;
    private UserResponse user;

    private String token;

    private long expireAt;

    public boolean isExpired() {
        return expireAt < new Date().getTime();
    }
}
