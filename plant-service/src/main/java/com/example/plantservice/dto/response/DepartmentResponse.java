package com.example.plantservice.dto.response;

import lombok.Data;

@Data
public class DepartmentResponse {
    private Long id;
    private String departmentName;
    private Boolean status;
}
