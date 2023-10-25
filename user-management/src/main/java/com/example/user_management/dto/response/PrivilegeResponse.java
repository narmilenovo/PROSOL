package com.example.user_management.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class PrivilegeResponse {
    private Long id;
    private String name;
    private Boolean status;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
