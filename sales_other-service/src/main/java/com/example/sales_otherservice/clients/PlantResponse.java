package com.example.sales_otherservice.clients;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantResponse {
    private Long id;
    private String plantCode;
    private String plantName;
    private Boolean status;
}
