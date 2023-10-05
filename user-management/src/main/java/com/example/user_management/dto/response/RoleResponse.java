package com.example.user_management.dto.response;

import lombok.Data;

import java.util.Set;

@Data
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private String plantName;
    private Boolean status;
    private Set<PrivilegeResponse> privileges;
}
