package com.example.generalservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class IndustrySectorResponse {
    private Long id;
    private String sectorCode;
    private String sectorName;
    private Boolean sectorStatus;
}
