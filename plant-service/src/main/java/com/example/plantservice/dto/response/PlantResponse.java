package com.example.plantservice.dto.response;


import lombok.Data;

@Data
public class PlantResponse {
    private Long id;
    private String plantCode;
    private String plantName;
    private Boolean status;
}
