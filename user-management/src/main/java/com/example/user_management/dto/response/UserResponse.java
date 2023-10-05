package com.example.user_management.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String business;
    private Long departmentId;
    private String plant;
    private Boolean status;
    private Set<RoleResponse> roles;
}
