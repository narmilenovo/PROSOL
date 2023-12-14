package com.example.user_management.client;

import com.example.user_management.dto.response.RoleResponse;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
public class UserDepartmentResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String business;
    private DepartmentResponse department;
    private List<Long> plantId;
    private Boolean status;
    private Set<RoleResponse> roles;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    //    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss a")
    private Date updatedAt;
}
