package com.example.plantservice.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantRequest {
    private String plantCode;
    private String plantName;
    private Boolean status;
}
