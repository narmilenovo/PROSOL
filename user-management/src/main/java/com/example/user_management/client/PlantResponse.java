package com.example.user_management.client;


import lombok.Data;

import java.util.Date;

@Data
public class PlantResponse {
    private Long id;
    private String plantCode;
    private String plantName;
    private Boolean status;
    private String createdBy;
    private String updatedBy;
    private Date createdAt;
    private Date updatedAt;
}
